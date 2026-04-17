package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.Alignment;

/**
 * Symbol element on a road sign.
 *
 * @param localX    local X coordinate
 * @param localY    local Y coordinate
 * @param alignment alignment of the symbol
 * @param symbol    symbol to display
 */
public record SymbolElement(
        int localX,
        int localY,
        Alignment alignment,
        Symbol symbol
) implements RoadSignElement {
    public SymbolElement withSymbol(Symbol symbol) {
        return new SymbolElement(localX(), localY(), alignment(), symbol);
    }

    public SymbolElement withPosition(int localX, int localY) {
        return new SymbolElement(localX, localY, alignment(), symbol);
    }

    public SymbolElement withAlignment(Alignment alignment) {
        return new SymbolElement(localX(), localY(), alignment, symbol);
    }
}
