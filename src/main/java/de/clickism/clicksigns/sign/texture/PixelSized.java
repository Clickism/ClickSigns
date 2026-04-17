package de.clickism.clicksigns.sign.texture;

import de.clickism.clicksigns.util.Constants;

/**
 * Interface for objects that have dimensions in pixels and blocks.
 */
public interface PixelSized {
    /**
     * Width in pixels
     *
     * @return width in pixels
     */
    int width();

    /**
     * Height in pixels
     *
     * @return height in pixels
     */
    int height();

    /**
     * Width in blocks
     *
     * @return width in blocks
     */
    default float blockWidth() {
        return (float) width() / Constants.BLOCK_PIXELS;
    }

    /**
     * Height in blocks
     *
     * @return height in blocks
     */
    default float blockHeight() {
        return (float) height() / Constants.BLOCK_PIXELS;
    }
}
