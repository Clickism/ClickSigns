package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.texture.Texture;

public final class SymbolElement extends RoadSignElement {
    private final Texture texture;

    public SymbolElement(int x, int y, Texture texture) {
        super(x, y);
        this.texture = texture;
    }

    public Texture texture() {
        return texture;
    }
}
