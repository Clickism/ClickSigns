package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.texture.Texture;

/**
 * Symbol element on a road sign.
 */
public final class SymbolElement extends RoadSignElement {
    private final Texture texture;

    /**
     * Creates a new symbol element with the given properties.
     *
     * @param localX  local X coordinate
     * @param localY  local Y coordinate
     * @param texture texture to display
     */
    public SymbolElement(int localX, int localY, Texture texture) {
        super(localX, localY);
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

    public SymbolElement withTexture(Texture newTexture) {
        return new SymbolElement(localX(), localY(), newTexture);
    }

    public SymbolElement withPosition(int localX, int localY) {
        return new SymbolElement(localX, localY, texture);
    }
}
