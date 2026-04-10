package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;

/**
 * Widget for a symbol element of a road sign
 */
public class SymbolElementWidget extends TextureWidget implements ElementProvider {
    private SymbolElement symbol;

    /**
     * Creates a new symbol widget.
     */
    public SymbolElementWidget(int anchorX, int anchorY, SymbolElement symbol) {
        super(anchorX, anchorY, symbol.texture());
        this.symbol = symbol;
        // Calculate position
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, symbol, this.width, this.height);
        // Need to move up by half a pixel to align for some reason???
        this.setPosition(pos.x, pos.y - TEXTURE_RENDER_SCALE / 2);
    }

    @Override
    public RoadSignElement element() {
        return symbol;
    }
}
