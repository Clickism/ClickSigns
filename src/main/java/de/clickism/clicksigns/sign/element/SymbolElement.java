package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;

/**
 * Symbol element on a road sign.
 *
 * @param x             local X coordinate
 * @param y             local Y coordinate
 * @param alignment     alignment of the symbol
 * @param symbolId      id of the root symbol
 * @param textureSource texture of the symbol, can diverge from the root via texture overrides, etc.
 */
public record SymbolElement(
    int x,
    int y,
    Alignment alignment,
    ResourceLocation symbolId,
    TextureSource textureSource
) implements SignElement {
    /**
     * Type key
     */
    public static final String TYPE = "symbol";

    /**
     * Creates a new symbol element with default properties.
     *
     * @return the default symbol element
     */
    public static SymbolElement createDefault() {
        var symbolId = ClickSigns.signAsset("symbols/arrows/right_curvy.png");
        return new SymbolElement(0, 0,
            Alignment.CENTER,
            symbolId,
            SignRegistries.SYMBOLS.get(symbolId).textureSource()
        );
    }

    @Override
    public String typeKey() {
        return TYPE;
    }

    /**
     * Resolves the symbol from the registry using the symbol ID.
     *
     * @return the resolved symbol, or null if not found
     */
    public Symbol resolveSymbol() {
        return SignRegistries.SYMBOLS.get(symbolId);
    }

    @Override
    public float width() {
        return textureSource.resolve(ColorResolver.empty()).width();
    }

    @Override
    public float height() {
        return textureSource.resolve(ColorResolver.empty()).height();
    }

    /**
     * Creates a new symbol element with the given texture source, keeping the other properties the same.
     * If the base of the texture source matches a different symbol in the registry, it will switch to that symbol.
     *
     * @param textureSource new texture source for the symbol
     * @return a new symbol element with the given texture source, keeping the other properties the same
     */
    public SymbolElement withTextureSource(TextureSource textureSource) {
        // If base matches up, we keep the symbolId
        var base = textureSource.base();
        var symbolId = this.symbolId();
        if (!base.equals(symbolId) && SignRegistries.SYMBOLS.has(base)) {
            symbolId = base; // Switch to the new symbol if it exists in the registry
        }
        return new SymbolElement(x(), y(), alignment(), symbolId, textureSource);
    }

    /**
     * Creates a new symbol element with the given position, keeping the other properties the same.
     *
     * @param x local X coordinate
     * @param y local Y coordinate
     * @return a new symbol element with the given position, keeping the other properties the same
     */
    public SymbolElement withPosition(int x, int y) {
        return new SymbolElement(x, y, alignment(), symbolId, textureSource);
    }

    /**
     * Creates a new symbol element with the given alignment, keeping the other properties the same.
     *
     * @param alignment alignment of the symbol
     * @return a new symbol element with the given alignment, keeping the other properties the same
     */
    public SymbolElement withAlignment(Alignment alignment) {
        return new SymbolElement(x(), y(), alignment, symbolId, textureSource);
    }
}
