package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.texture.Texture;

public final class SymbolElement extends RoadSignElement {
    private final Texture texture;

    public SymbolElement(int localX, int localY, Texture texture) {
        super(localX, localY);
        this.texture = texture;
    }

    public Texture texture() {
        return texture;
    }
}
