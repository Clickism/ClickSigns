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
    /**
     * Type key
     */
    public static final String TYPE = "symbol";

    @Override
    public String typeKey() {
        return TYPE;
    }

    /**
     * Creates a new symbol element with the given symbol, keeping the other properties the same.
     *
     * @param symbol symbol to display
     * @return a new symbol element with the given symbol, keeping the other properties the same
     */
    public SymbolElement withSymbol(Symbol symbol) {
        return new SymbolElement(localX(), localY(), alignment(), symbol);
    }

    /**
     * Creates a new symbol element with the given position, keeping the other properties the same.
     *
     * @param localX local X coordinate
     * @param localY local Y coordinate
     * @return a new symbol element with the given position, keeping the other properties the same
     */
    public SymbolElement withPosition(int localX, int localY) {
        return new SymbolElement(localX, localY, alignment(), symbol);
    }

    /**
     * Creates a new symbol element with the given alignment, keeping the other properties the same.
     *
     * @param alignment alignment of the symbol
     * @return a new symbol element with the given alignment, keeping the other properties the same
     */
    public SymbolElement withAlignment(Alignment alignment) {
        return new SymbolElement(localX(), localY(), alignment, symbol);
    }
}
