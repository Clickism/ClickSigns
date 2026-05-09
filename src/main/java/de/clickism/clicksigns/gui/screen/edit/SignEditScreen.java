package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.SignWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class SignEditScreen extends BaseScreen {
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_PADDING = 10;
    private final RoadSign roadSign;

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

        // Add panels
        var leftPanel = new PanelWidget(-PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(leftPanel);

        var rightPanel = new PanelWidget(width - PANEL_WIDTH + PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(rightPanel);
    }
}
