package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.Size;

/**
 * Plate element on a road sign.
 * Plate elemens are used to display a solid sign texture, and offer a more flexible way to design
 * signs as opposed to placing two signs next to each other.
 *
 * @param x                 the X coordinate of the plate element
 * @param y                 the Y coordinate of the plate element
 * @param alignment         the alignment of the plate element
 * @param frontSource       the texture source for the front of the plate element
 * @param backSource        the texture source for the back of the plate element
 * @param matchSignTextures whether the plate element should match the sign textures
 */
public record PlateElement(
    int x,
    int y,
    Alignment alignment,
    TextureSource frontSource,
    TextureSource backSource,
    boolean matchSignTextures
) implements SignElement {
    /**
     * Type key
     */
    public static final String TYPE = "plate";
    public static final Size MIN_PLATE_SIZE = new Size(4, 4);
    public static final Size MAX_PLATE_SIZE = RoadSign.MAX_SIGN_SIZE;

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
        return new PlateElement(x, y, alignment, frontSource, backSource, matchSignTextures);
    }

    @Override
    public PlateElement withAlignment(Alignment alignment) {
        return new PlateElement(x, y, alignment, frontSource, backSource, matchSignTextures);
    }

    /**
     * Creates a new PlateElement with the specified front texture source.
     *
     * @param front The new front texture source for the PlateElement.
     * @return A new PlateElement instance with the updated front texture source.
     */
    public PlateElement withFrontSource(TextureSource front) {
        return new PlateElement(x, y, alignment, front, backSource, matchSignTextures);
    }

    /**
     * Creates a new PlateElement with the specified back texture source.
     *
     * @param back The new back texture source for the PlateElement.
     * @return A new PlateElement instance with the updated back texture source.
     */
    public PlateElement withBackSource(TextureSource back) {
        return new PlateElement(x, y, alignment, frontSource, back, matchSignTextures);
    }

    /**
     * Creates a new PlateElement with the specified matchSignTextures value.
     *
     * @param matchSignTextures The new matchSignTextures value for the PlateElement.
     * @return A new PlateElement instance with the updated matchSignTextures value.
     */
    public PlateElement withMatchSignTextures(boolean matchSignTextures) {
        return new PlateElement(x, y, alignment, frontSource, backSource, matchSignTextures);
    }
}
