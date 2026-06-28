package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.screen.edit.widget.*;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.SignWidget;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.util.Size;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

// TODO: Add guidelines when dragging
// TODO: Make a common utility when converting between coordinate spaces (screen, sign, element)

/**
 * Screen for editing a road sign in an advanced way.
 * Supports resizing, adding/removing/editing elements, and changing textures.
 */
public class SignEditScreen extends BaseScreen {
    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_PADDING = 8;
    private static final int MIN_SIGN_SIZE = 8;

    private RoadSign roadSign;
    private final Consumer<RoadSign> onUpdate;
    private boolean dirty = false;

    private final EditContext editContext = new EditContext();

    private SignWidget signWidget;

    // Dragging state
    double dragStartMouseX;
    double dragStartMouseY;
    int dragStartElementX;
    int dragStartElementY;

    /**
     * Creates a new road sign edit screen
     *
     * @param roadSign the road sign to edit
     * @param onUpdate the callback to call when the road sign is updated
     * @param parent   the parent screen, can be null
     */
    public SignEditScreen(RoadSign roadSign, Consumer<RoadSign> onUpdate, @Nullable Screen parent) {
        super(parent);
        this.roadSign = roadSign;
        this.onUpdate = onUpdate;
    }

    @Override
    protected void init() {
        // Add road sign texture
        // TODO: Use custom text and symbol widgets
        this.signWidget = new SignWidget(0, 0, roadSign,
                (anchorX, anchorY, element, colorResolver, signWidth) ->
                        new EditTextWidget(anchorX, anchorY, element, colorResolver, signWidth, editContext),
                (anchorX, anchorY, element, colorResolver, screen) ->
                        new EditSymbolWidget(anchorX, anchorY, element, colorResolver, screen, editContext),
                this);
        this.addRenderableWidget(signWidget);

        var sizeWidget = new StringWidget(0, 0, 100, 20, Component.literal("Size: " + roadSign.width() + " x " + roadSign.height()), GuiUtils.font());
        this.addRenderableWidget(sizeWidget);

        var confirmButton = Button.builder(
                Component.literal("✔ ").append(Component.translatable("clicksigns.text.confirm")),
                b -> GuiUtils.closeScreen()
        ).build();
        this.addRenderableWidget(confirmButton);

        LinearLayout.vertical()
                .add(signWidget)
                .addSpacing(48)
                .add(sizeWidget)
                .add(confirmButton)
                .center()
                .layout(halfWidth(), halfHeight());

        if (roadSign.frontSource().canResize()) {
            var resizeControls = new ResizeControls(signWidget, this::handleResize, this::canResize);
            addRenderableWidget(resizeControls);
        }

        // Sign panel
        var signPanel = new PanelWidget(-PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(signPanel);

        if (editContext.dragging()) {
            // Guidelines
            var guidelines = signWidget.new GuidelinesWidget();
            addRenderableOnly(guidelines);
        }

        var padding = 4;

        var centerX = roadSign.width() / 2;
        var centerY = roadSign.height() / 2;

        LinearLayout.vertical()
                .padding(padding)
                .centerHorizontal()
                .composer(PANEL_WIDTH - PANEL_PADDING * 2)
                // Add sections
                .bigHeader(Component.literal("Sign Properties"))
                .header(Component.literal("Textures"))
                .widget(new TexturePropertiesWidget(0, 0, this, roadSign, this::roadSign))
                .header(Component.literal("Elements"))
                .button(Component.literal("+ Add Symbol"), b -> {
                    var symbol = SignRegistries.SYMBOLS.get(RoadSign.DEFAULT_SYMBOL_TEXTURE);
                    var element = new SymbolElement(centerX, centerY, Alignment.CENTER, symbol);
                    this.roadSign(roadSign.addElement(element));
                })
                .button(Component.literal("+ Add Text"), b -> {
                    var element = new TextElement(centerX, centerY, Alignment.TEXT_RIGHT, "", 1f, "foreground", null);
                    this.roadSign(roadSign.addElement(element));
                })
                .header(Component.literal("Tools"))
                .coloredButton(Color.BLUE, Component.literal("⏪ Reset Texts"), b -> {
                    this.roadSign(roadSign.withElements(roadSign.elements().stream()
                            .map(element -> {
                                if (element instanceof TextElement text) {
                                    return text.withText("");
                                }
                                return element;
                            }).toList()));
                })
                .coloredButton(Color.RED, Component.literal("🗑 Remove Elements"), b -> {
                    this.roadSign(roadSign.withElements(List.of()));
                })
                // Lay out in the center of the panel
                .layout(PANEL_WIDTH / 2, PANEL_PADDING)
                .compose(this::addRenderableWidget);

        // Element panel
        var elementPanel = new PanelWidget(width - PANEL_WIDTH, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(elementPanel);

        var composer = LinearLayout.vertical()
                .padding(padding)
                .centerHorizontal()
                .composer(PANEL_WIDTH - PANEL_PADDING * 2)
                // Add sections
                .bigHeader(Component.literal("Element Properties"));

        var selectedElement = editContext.selectedElement();
        if (selectedElement != null) {
            if (selectedElement instanceof TextElement textElement) {
                new TextElementPropertiesComposer(composer, roadSign.width(), roadSign.colorResolver(), textElement,
                        newTextElement -> {
                            this.roadSign(roadSign.replaceElement(selectedElement, newTextElement));
                            this.editContext.selectElement(newTextElement);
                        })
                        .compose();
            } else if (selectedElement instanceof SymbolElement symbolElement) {
                new SymbolElementPropertiesComposer(composer, this, roadSign.colorResolver(), symbolElement,
                        newSymbolElement -> {
                            this.roadSign(roadSign.replaceElement(selectedElement, newSymbolElement));
                            this.editContext.selectElement(newSymbolElement);
                        })
                        .compose();
            }
            composer.header(Component.literal("Other"))
                    .coloredButton(Color.RED, Component.literal("🗑 Delete Element"), button -> {
                        this.roadSign(roadSign.removeElement(selectedElement));
                        this.editContext.selectElement(null);
                    });
        } else {
            composer.text(Component.literal("No element selected"));

        }

        // Lay out in the center of the panel
        composer.layout(width - PANEL_WIDTH / 2, PANEL_PADDING)
                .compose(this::addRenderableWidget);
    }

    /**
     * Handle resizing the sign when the resize controls are clicked
     */
    private void handleResize(ResizeControls.Direction direction) {
        var size = calculateNewSize(direction);
        this.roadSign(roadSign.resized(size.width(), size.height()));
    }

    /**
     * Check if we can resize in the given direction
     *
     * @param direction the direction to check
     * @return true if resizing in the given direction would change the size of the sign, false otherwise
     */
    private boolean canResize(ResizeControls.Direction direction) {
        var size = calculateNewSize(direction);
        return size.width() != roadSign.width() || size.height() != roadSign.height();
    }

    private Size calculateNewSize(ResizeControls.Direction direction) {
        var width = roadSign.width();
        var height = roadSign.height();
        var step = 16;
        // If the sign is less than the cutoff, resize by half the step
        var halfStepCutoff = step * 2;
        // Min size of a sign
        var stepVertical = height < halfStepCutoff ? step / 2 : step;
        var stepHorizontal = width < halfStepCutoff ? step / 2 : step;
        switch (direction) {
            case UP -> height += stepVertical;
            case RIGHT -> width += stepHorizontal;
            case DOWN -> height -= stepVertical;
            case LEFT -> width -= stepHorizontal;
        }
        // Don't allow resizing below the minimum size
        // We don't clamp here as to not break the size scale, in case the starting size is not a multiple of the step
        if (width < MIN_SIGN_SIZE) {
            width = roadSign.width();
        }
        if (height < MIN_SIGN_SIZE) {
            height = roadSign.height();
        }
        // Return resized dimensions
        return new Size(width, height);
    }

    /**
     * Set the road sign being edited and mark the screen as dirty
     *
     * @param roadSign the new road sign
     */
    private void roadSign(RoadSign roadSign) {
        this.roadSign = roadSign;
        this.onUpdate.accept(roadSign);
        this.dirty = true;
    }

    @Override
    public void tick() {
        if (dirty || editContext.isDirty()) {
            this.dirty = false;
            editContext.clearDirty();
            this.rebuildWidgets();
        }
        super.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        var res = super.mouseClicked(mouseX, mouseY, button); // Let children process first
        var element = editContext.selectedElement();
        if (element == null) {
            // No selected element, don't start dragging
            editContext.dragging(false);
            return res;
        }
        // Start dragging
        dragStartMouseX = mouseX;
        dragStartMouseY = mouseY;
        dragStartElementX = element.localX();
        dragStartElementY = element.localY();
        // Don't start dargging here, let child handle
        return res;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (editContext.dragging()) {
            // Stop dragging
            editContext.dragging(false);
            this.dirty = true;
        }
        return super.mouseReleased(d, e, i);
    }

    @Override
    public boolean mouseDragged(double fromX, double fromY, int button, double deltaX, double deltaY) {
        var element = editContext.selectedElement();
        if (element == null || !editContext.dragging()) {
            return super.mouseDragged(fromX, fromY, button, deltaX, deltaY);
        }
        // Get total delta since drag started
        int diffX = (int) ((fromX - dragStartMouseX) / DEFAULT_TEXTURE_RENDER_SCALE);
        int diffY = (int) (-(fromY - dragStartMouseY) / DEFAULT_TEXTURE_RENDER_SCALE);

        var newX = dragStartElementX + diffX;
        var newY = dragStartElementY + diffY;

        newX = Mth.clamp(newX, 0, roadSign.width());
        newY = Mth.clamp(newY, 0, roadSign.height());

        if (newX == element.localX() && newY == element.localY()) {
            return super.mouseDragged(fromX, fromY, button, deltaX, deltaY);
        }

        element = element.withPosition(newX, newY);
        this.roadSign(roadSign.replaceElement(editContext.selectedElement(), element));
        editContext.selectElement(element);
        // Rerender immediately for smooth dragging
        editContext.clearDirty();
        this.rebuildWidgets();

        return super.mouseDragged(fromX, fromY, button, deltaX, deltaY);
    }
}
