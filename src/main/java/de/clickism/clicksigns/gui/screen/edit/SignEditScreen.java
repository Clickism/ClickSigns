package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.SignWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * Screen for editing a road sign in an advanced way.
 * Supports resizing, adding/removing/editing elements, and changing textures.
 */
public class SignEditScreen extends BaseScreen {
    private static final int PANEL_WIDTH = 150;
    private static final int PANEL_PADDING = 10;

    private RoadSign roadSign;
    private boolean dirty = false;

    /**
     * Creates a new road sign edit screen
     *
     * @param roadSign the road sign to edit
     * @param parent   the parent screen, can be null
     */
    public SignEditScreen(RoadSign roadSign, @Nullable Screen parent) {
        super(parent);
        this.roadSign = roadSign;
    }

    @Override
    protected void init() {
        // Add road sign texture
        // TODO: Use custom text and symbol widgets
        var signWidget = new SignWidget(0, 0, roadSign, TextWidget::new, SymbolWidget::new, this);
        this.addRenderableWidget(signWidget);
        LinearLayout.vertical()
                .add(signWidget)
                .center()
                .layout(halfWidth(), halfHeight());

        var resizeControls = new ResizeControls(signWidget, this::handleResize);
        addRenderableWidget(resizeControls);

        // Add panels
        var leftPanel = new PanelWidget(-PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(leftPanel);

        var rightPanel = new PanelWidget(width - PANEL_WIDTH + PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(rightPanel);
    }

    /**
     * Handle resizing the sign when the resize controls are clicked
     */
    private void handleResize(ResizeControls.Direction direction) {
        var width = roadSign.width();
        var height = roadSign.height();
        var step = 16;
        // If the sign is less than the cutoff, resize by half the step
        var halfStepCutoff = step * 2;
        // Min size of a sign
        var minSize = 8;
        var stepVertical = height < halfStepCutoff ? step / 2 : step;
        var stepHorizontal = width < halfStepCutoff ? step / 2 : step;
        switch (direction) {
            case UP -> height += stepVertical;
            case RIGHT -> width += stepHorizontal;
            case DOWN -> height -= stepVertical;
            case LEFT -> width -= stepHorizontal;
        }
        width = Math.max(width, minSize);
        height = Math.max(height, minSize);
        this.roadSign(roadSign.resized(width, height));
    }

    /**
     * Set the road sign being edited and mark the screen as dirty
     *
     * @param roadSign the new road sign
     */
    private void roadSign(RoadSign roadSign) {
        this.roadSign = roadSign;
        this.dirty = true;
    }

    @Override
    public void tick() {
        if (dirty) {
            this.dirty = false;
            this.rebuildWidgets();
        }
        super.tick();
    }
}
