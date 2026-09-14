package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.Size;

public record PlateElement(
    int x,
    int y,
    Alignment alignment,
    TextureSource frontSource,
    TextureSource backSource
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
    public float width() {
        return frontSource.resolve(ColorResolver.empty()).width();
    }

    @Override
    public float height() {
        return frontSource.resolve(ColorResolver.empty()).height();
    }

    /**
     * Calculates the size of the plate element in pixels.
     * Not lossy, since the width and height are already in pixels.
     *
     * @return The size of the plate element in pixels.
     */
    public Size size() {
        return new Size((int) width(), (int) height());
    }

    @Override
    public PlateElement withPosition(int x, int y) {
        return new PlateElement(x, y, alignment(), frontSource(), backSource());
    }

    public PlateElement withAlignment(Alignment alignment) {
        return new PlateElement(x(), y(), alignment, frontSource(), backSource());
    }

    public PlateElement withFrontSource(TextureSource front) {
        return new PlateElement(x(), y(), alignment(), front, backSource());
    }

    public PlateElement withBackSource(TextureSource back) {
        return new PlateElement(x(), y(), alignment(), frontSource(), back);
    }
}
