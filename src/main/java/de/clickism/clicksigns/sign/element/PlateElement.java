package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureSource;

public record PlateElement(
        int localX,
        int localY,
        Alignment alignment,
        TextureSource front,
        TextureSource back
) implements SignElement {
    /**
     * Type key
     */
    public static final String TYPE = "plate";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public int signWidth() {
        return front.resolve(ColorResolver.empty()).width();
    }

    @Override
    public int signHeight() {
        return front.resolve(ColorResolver.empty()).height();
    }

    @Override
    public SignElement withPosition(int localX, int localY) {
        return new PlateElement(localX, localY, alignment(), front(), back());
    }
}
