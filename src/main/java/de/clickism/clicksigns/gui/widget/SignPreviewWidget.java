package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;

public class SignPreviewWidget extends NestedWidget {
    public SignPreviewWidget(int x, int y, RoadSign roadSign) {
        super(x, y);
        if (roadSign == null) return;
        roadSign(roadSign);
    }

    public void roadSign(RoadSign roadSign) {
        this.children().clear();
        // Add road sign texture
        var textureWidget = new TextureWidget(this.getX(), this.getY(), roadSign.frontTexture());
        this.addChildNoUpdate(textureWidget);
        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = new SymbolElementWidget(anchorX, anchorY, symbol, roadSign.colorResolver(), null);
                symbolWidget.makeUneditable();
                this.addChildNoUpdate(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = new TextElementWidget(anchorX, anchorY, textElement, roadSign.colorResolver());
                textBox.makeUneditable();
                this.addChildNoUpdate(textBox);
            }
        }
        this.updateSize();
    }
}
