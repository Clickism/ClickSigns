package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.sign.texture.Texture;

/**
 * Symbol element on a road sign.
 *
 * @param localX    local X coordinate
 * @param localY    local Y coordinate
 * @param alignment alignment of the symbol
 * @param texture   texture to display
 */
public record SymbolElement(
        int localX,
        int localY,
        Alignment alignment,
        Texture texture
) implements RoadSignElement {
    public SymbolElement withTexture(Texture texture) {
        return new SymbolElement(localX(), localY(), alignment(), texture);
    }

    public SymbolElement withPosition(int localX, int localY) {
        return new SymbolElement(localX, localY, alignment(), texture);
    }

    public SymbolElement withAlignment(Alignment alignment) {
        return new SymbolElement(localX(), localY(), alignment, texture);
    }
}
