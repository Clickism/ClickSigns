package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolElementWidget;
import de.clickism.clicksigns.gui.widget.element.TextElementWidget;
import de.clickism.clicksigns.gui.widget.texture.TextureWidget;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * Widget for rendering a road sign with its texture and elements.
 */
public class SignWidget extends NestedWidget {
    protected final Screen parent;

    /**
     * Creates a new sign widget at the given position, displaying the given road sign.
     *
     * @param x        the x position of the widget
     * @param y        the y position of the widget
     * @param roadSign the road sign to display, or null to create an empty widget
     * @param parent   the parent screen for symbol menus
     */
    public SignWidget(int x, int y, RoadSign roadSign, @Nullable Screen parent) {
        super(x, y);
        this.parent = parent;
        if (roadSign == null) return;
        roadSign(roadSign);
    }

    /**
     * Changes the displayed road sign of this widget.
     *
     * @param roadSign the new road sign to display
     */
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
                var symbolWidget = createSymbolElementWidget(anchorX, anchorY, symbol, roadSign);
                this.addChildNoUpdate(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = createTextElementWidget(anchorX, anchorY, textElement, roadSign);
                this.addChildNoUpdate(textBox);
            }
        }
        this.updateSize();
    }

    /**
     * Creates a widget for the given text element and road sign.
     */
    protected TextElementWidget createTextElementWidget(int anchorX, int anchorY, TextElement textElement, RoadSign roadSign) {
        var textBox = new TextElementWidget(anchorX, anchorY, textElement, roadSign.colorResolver(), roadSign.frontTexture().width());
        textBox.makeUneditable();
        return textBox;
    }

    /**
     * Creates a widget for the given symbol element and road sign.
     */
    protected SymbolElementWidget createSymbolElementWidget(int anchorX, int anchorY, SymbolElement symbolElement, RoadSign roadSign) {
        var symbolWidget = new SymbolElementWidget(anchorX, anchorY, symbolElement, roadSign.colorResolver(), parent);
        symbolWidget.makeUneditable();
        return symbolWidget;
    }
}
