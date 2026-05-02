package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.widget.PanelWidget;
import de.clickism.clicksigns.gui.widget.SymbolElementWidget;
import de.clickism.clicksigns.gui.widget.TextElementWidget;
import de.clickism.clicksigns.gui.widget.TextureWidget;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

public class RoadSignEditScreen extends BaseScreen {
    private static final int PANEL_WIDTH = 200;
    private static final int PANEL_PADDING = 10;
    private final RoadSign roadSign;

    protected RoadSignEditScreen(RoadSign roadSign, @Nullable Screen parent) {
        super(parent);
        this.roadSign = roadSign;
    }

    @Override
    protected void init() {
        var halfWidth = width / 2;
        var halfHeight = height / 2;
        // Add road sign texture
        var textureWidget = new TextureWidget(halfWidth, halfHeight, roadSign.frontTexture());
        textureWidget.center();
        this.addRenderableWidget(textureWidget);
        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = new SymbolElementWidget(anchorX, anchorY, symbol, roadSign.colorResolver(), this);
                this.addRenderableWidget(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = new TextElementWidget(anchorX, anchorY, textElement, roadSign.colorResolver(), roadSign.frontTexture().width());
                this.addRenderableWidget(textBox);
            }
        }

        // Add panels
        var leftPanel = new PanelWidget(-PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(leftPanel);

        var rightPanel = new PanelWidget(width - PANEL_WIDTH + PANEL_PADDING, -PANEL_PADDING, PANEL_WIDTH + PANEL_PADDING, height + PANEL_PADDING * 2);
        addRenderableWidget(rightPanel);
    }
}
