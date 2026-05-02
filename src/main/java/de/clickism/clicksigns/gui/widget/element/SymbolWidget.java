package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.gui.widget.texture.ClickableTextureWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import net.minecraft.client.gui.screens.Screen;

import static de.clickism.clicksigns.gui.GuiUtils.OUTLINE_COLOR;

/**
 * Widget for a symbol element of a road sign
 */
public class SymbolWidget extends ClickableTextureWidget implements ElementProvider {
    protected final int anchorX;
    protected final int anchorY;
    protected SymbolElement symbol;
    protected final ColorResolver colorResolver;
    protected final Screen parent;

    /**
     * Creates a new symbol widget.
     *
     * @param anchorX the x position to anchor the element on the sign
     * @param anchorY the y position to anchor the element on the sign
     * @param symbol  the symbol element to display
     * @param parent  the parent screen, used for going back from the symbol menu
     */
    public SymbolWidget(int anchorX, int anchorY, SymbolElement symbol, ColorResolver colorResolver, Screen parent) {
        this(anchorX, anchorY, symbol, colorResolver, OUTLINE_COLOR, parent);
    }

    /**
     * Creates a new symbol widget with a custom outline color.
     *
     * @param anchorX       the x position to anchor the element on the sign
     * @param anchorY       the y position to anchor the element on the sign
     * @param symbol        the symbol element to display
     * @param colorResolver the color resolver to use for resolving the symbol texture
     * @param outlineColor  the color of the outline to render on hover
     * @param parent        the parent screen, used for going back from the symbol menu
     */
    public SymbolWidget(int anchorX, int anchorY, SymbolElement symbol, ColorResolver colorResolver, int outlineColor, Screen parent) {
        super(anchorX, anchorY, symbol.symbol().texture().resolve(colorResolver), outlineColor);
        this.colorResolver = colorResolver;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.symbol = symbol;
        this.parent = parent;
        this.updatePosition();
    }

    @Override
    public SignElement element() {
        return symbol;
    }

    /**
     * Changes the displayed symbol of this widget.
     *
     * @param symbol the new symbol to display
     */
    protected void symbol(SymbolElement symbol) {
        this.symbol = symbol;
        this.texture(symbol.symbol().texture().resolve(colorResolver));
        updatePosition();
    }

    /**
     * Updates the position of the widget based on the anchor and the size of the symbol.
     */
    protected void updatePosition() {
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, symbol, this.width, this.height);
        this.setPosition(pos.x, pos.y);
    }
}
