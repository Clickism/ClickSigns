package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.Symbol;

/**
 * Symbol element on a road sign.
 *
 * @param x    local X coordinate
 * @param y    local Y coordinate
 * @param alignment alignment of the symbol
 * @param symbol    symbol to display
 */
public record SymbolElement(
    int x,
    int y,
    Alignment alignment,
    Symbol symbol
) implements SignElement {
    /**
     * Type key
     */
    public static final String TYPE = "symbol";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public float width() {
        return symbol.texture().resolve(ColorResolver.empty()).width();
    }

    @Override
    public float height() {
        return symbol.texture().resolve(ColorResolver.empty()).height();
    }

    /**
     * Creates a new symbol element with the given symbol, keeping the other properties the same.
     *
     * @param symbol symbol to display
     * @return a new symbol element with the given symbol, keeping the other properties the same
     */
    public SymbolElement withSymbol(Symbol symbol) {
        return new SymbolElement(x(), y(), alignment(), symbol);
    }

    /**
     * Creates a new symbol element with the given position, keeping the other properties the same.
     *
     * @param x local X coordinate
     * @param y local Y coordinate
     * @return a new symbol element with the given position, keeping the other properties the same
     */
    public SymbolElement withPosition(int x, int y) {
        return new SymbolElement(x, y, alignment(), symbol);
    }

    /**
     * Creates a new symbol element with the given alignment, keeping the other properties the same.
     *
     * @param alignment alignment of the symbol
     * @return a new symbol element with the given alignment, keeping the other properties the same
     */
    public SymbolElement withAlignment(Alignment alignment) {
        return new SymbolElement(x(), y(), alignment, symbol);
    }
}
