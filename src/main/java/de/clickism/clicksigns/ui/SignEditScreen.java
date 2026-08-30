package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.*;
import de.clickism.clickui.reactivity.State;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

public class SignEditScreen extends UiScreen {
    private @Nullable SignElement selected = null;
    private Consumer<RoadSign> onSignUpdate = sign -> {};

    private final Ref<SignView> signViewRef = ref();
    private final Ref<SignControls> signControlsRef = ref();
    private final Ref<SignEditor> signEditorRef = ref();
    private final Ref<ElementControls> elementControlsRef = ref();

    private int dragStartX = 0;
    private int dragStartY = 0;

    private RoadSign sign;

    public SignEditScreen(@NotNull RoadSign sign) {
        this.sign = sign;
    }

    public SignEditScreen onSignUpdate(Consumer<RoadSign> onSignUpdate) {
        this.onSignUpdate = onSignUpdate;
        return this;
    }

    private void sign(RoadSign sign) {
        this.sign = sign;
        this.signViewRef.get().roadSign(sign);
        // TODO: Invalidate or relayout?
    }

    private void selected(SignElement element) {
        this.selected = element;
        this.elementControlsRef.get().element.update(element);
    }

    @Override
    public Element<?> build() {
        var panelWidth = 120;

        return box()
            .grow()
            .horizontal()
            .children(
                // Left panel
                box()
                    .width(panelWidth)
                    .growHeight()
                    .padding(8)
                    .style(s -> s
                        .background(UiColor.BLACK_A50)
                        .border(UiColor.LIGHT_GRAY))
                    .children(
                        // Scroll container
                        box()
                            .grow()
                            .scrollable(true)
                            .children(
                                new SignControls()
                                    .ref(signControlsRef)
                                    .grow()
                            )
                    ),

                // Center editor
                // TODO: Make scrollable?
                box()
                    .grow()
                    .alignCenter()
                    .childGap(8)
                    .children(
                        new SignEditor()
                            .ref(signEditorRef)
                            .grow()
                            .alignCenter()
                    ),

                // Right panel
                box()
                    .width(panelWidth)
                    .growHeight()
                    .padding(8)
                    .style(s -> s
                        .background(UiColor.BLACK_A50)
                        .border(UiColor.LIGHT_GRAY))
                    .children(
                        // Scroll container
                        box()
                            .grow()
                            .scrollable(true)
                            .children(
                                new ElementControls()
                                    .ref(elementControlsRef)
                                    .grow()
                            )
                    )
            );
    }

    private class SignEditor extends Component<SignEditor> {

        @Override
        protected void build() {
            childGap(8);
            children(
                // TODO: Resize controls!
                // Sign view
                new SignView()
                    .ref(signViewRef)
                    .roadSign(sign)
                    .elementConfig((uiElement, signElement) -> {
                        uiElement
                            // Hover style
                            .style(s -> s
                                .whenHovered(h -> h
                                    .border(UiColor.RED)))
                            // Update selected on click
                            .onClick(event -> {
                                selected(signElement);
                            })
                            .onDragStart(event -> {
                                dragStartX = signElement.localX();
                                dragStartY = signElement.localY();
                            })
                            // Drag controls
                            .onDrag(event -> {
                                // Get the delta in sign space
                                int deltaX = (int) (event.totalDeltaX() / DEFAULT_TEXTURE_RENDER_SCALE);
                                int deltaY = (int) (event.totalDeltaY() / DEFAULT_TEXTURE_RENDER_SCALE);

                                int newX = dragStartX + deltaX;
                                int newY = dragStartY - deltaY;

                                if (newX == signElement.localX() && newY == signElement.localY()) {
                                    // No change
                                    return;
                                }

                                // TODO: Causes sign editor to rebuild, and we lose drag state
                                sign(sign.replaceElement(signElement, signElement.withPosition(newX, newY)));
                            });
                    }),
                // Show size
                text("Size: %d x %d".formatted(sign.width(), sign.height())),
                // Confirm
                button(ComponentUtil.confirmWithIcon())
                    .onClick(event -> {
                        // Callback and close
                        onSignUpdate.accept(sign);
                        close();
                    })
            );
        }
    }

    /**
     * The sign controls, for editing general info about ths sign,
     * such as textures or adding elements.
     */
    private class SignControls extends Component<SignControls> {
        @Override
        protected void build() {
            children(
                h2(t("clicksigns.editor.sign_properties")),
                // Add texture selection
                h4(t("clicksigns.editor.sign_textures")),

                // Add element controls
                h4(t("clicksigns.editor.elements")),
                // Add tools
                h4(t("clicksigns.editor.tools"))
            );
        }
    }

    /**
     * The element controls, meant for editing the
     * selected element.
     */
    private class ElementControls extends Component<ElementControls> {
        private final State<SignElement> element = state(null);

        @Override
        protected void build() {
            var element = this.element.get();

            if (element == null) {
                // No element selecteed
                add(text(t("clicksigns.editor.no_element_selected"))
                    .style(s -> s
                        .alpha(0.5f)));
                return;
            }

            add(h2(t("clicksigns.editor.element_properties")));

            // Delete button
            add(h4(t("clicksigns.editor.other")));
            add(button(t("🗑", "clicksigns.editor.tools.remove_element"))
                .onClick(event -> {
                    // TODO: Update sign (and other elements?)
                    sign(sign.removeElement(element));
                }));
        }
    }
}
