package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.SignWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolElementWidget;
import de.clickism.clicksigns.gui.widget.element.TextElementWidget;
import de.clickism.clicksigns.gui.widget.texture.TextureWidget;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
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
        var signWidget = new SignWidget(0, 0, roadSign, this);
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
