package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;

import java.util.ArrayList;
import java.util.List;

public class RoadSignWidget extends NestedWidget {
    private final List<ElementProvider> elementProviders = new ArrayList<>();

    public RoadSignWidget(int x, int y, RoadSign roadSign, boolean editable) {
        super(x, y);
        // Add road sign texture
        var textureWidget = new TextureWidget(x, y, roadSign.frontTexture());
        this.addChildNoUpdate(textureWidget);
        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        this.elementProviders.clear();
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = new SymbolElementWidget(anchorX, anchorY, symbol, roadSign.colorResolver(), null);
                if (!editable) {
                    symbolWidget.makeUneditable();
                }
                this.elementProviders.add(symbolWidget);
                this.addChildNoUpdate(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = new TextElementWidget(anchorX, anchorY, textElement, roadSign.colorResolver());
                if (!editable) {
                    textBox.makeUneditable();
                }
                this.elementProviders.add(textBox);
                this.addChildNoUpdate(textBox);
            }
        }
        this.updateSize();
    }
}
