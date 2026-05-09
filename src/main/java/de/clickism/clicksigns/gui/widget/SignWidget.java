package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
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
    protected final TextWidgetProvider textWidgetProvider;
    protected final SymbolWidgetProvider symbolWidgetProvider;

    /**
     * Creates a new sign widget at the given position, displaying the given road sign.
     *
     * @param x        the x position of the widget
     * @param y        the y position of the widget
     * @param roadSign the road sign to display, or null to create an empty widget
     * @param parent   the parent screen for symbol menus
     */
    public SignWidget(int x, int y, RoadSign roadSign, @Nullable Screen parent) {
        this(x, y, roadSign,
                (anchorX, anchorY, textElement, sign) ->
                        new TextWidget(anchorX, anchorY, textElement, sign.colorResolver(), sign.width()),
                (anchorX, anchorY, symbolElement, sign) ->
                        new SymbolWidget(anchorX, anchorY, symbolElement, sign.colorResolver(), parent),
                parent
        );
    }

    public SignWidget(
            int x, int y,
            @Nullable RoadSign roadSign,
            TextWidgetProvider textWidgetProvider,
            SymbolWidgetProvider symbolWidgetProvider,
            @Nullable Screen parent
    ) {
        super(x, y);
        this.parent = parent;
        this.textWidgetProvider = textWidgetProvider;
        this.symbolWidgetProvider = symbolWidgetProvider;
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
        this.addChild(textureWidget);
        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = symbolWidgetProvider.create(anchorX, anchorY, symbol, roadSign);
                this.addChild(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = textWidgetProvider.create(anchorX, anchorY, textElement, roadSign);
                this.addChild(textBox);
            }
        }
        this.updateSize();
    }

    /**
     * Factory interface for text widgets
     */
    public interface TextWidgetProvider {
        /**
         * Creates a new text widget
         */
        TextWidget create(int anchorX, int anchorY, TextElement textElement, RoadSign roadSign);
    }

    /**
     * Factory interface for symbol widgets
     */
    public interface SymbolWidgetProvider {
        /**
         * Creates a new symbol widget
         */
        SymbolWidget create(int anchorX, int anchorY, SymbolElement symbolElement, RoadSign roadSign);
    }
}
