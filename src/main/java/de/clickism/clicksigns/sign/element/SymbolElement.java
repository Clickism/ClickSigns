package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.util.texture.Texture;

/**
 * Symbol element on a road sign.
 */
public final class SymbolElement extends RoadSignElement {
    private final Texture texture;

    /**
     * Creates a new symbol element with the given properties.
     *
     * @param localX    local X coordinate
     * @param localY    local Y coordinate
     * @param alignment alignment of the symbol
     * @param texture   texture to display
     */
    public SymbolElement(int localX, int localY, Alignment alignment, Texture texture) {
        super(localX, localY, alignment);
        this.texture = texture;
    }

    /**
     * Gets the texture of this symbol element.
     *
     * @return texture
     */
    public Texture texture() {
        return texture;
    }

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
