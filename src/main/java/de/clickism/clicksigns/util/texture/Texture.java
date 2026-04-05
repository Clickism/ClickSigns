package de.clickism.clicksigns.util.texture;

import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2f;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Texture wrapper
 */
public interface Texture {
    /**
     * The texture of the image
     *
     * @return texture
     */
    ResourceLocation location();

    /**
     * Width of the image in pixels
     *
     * @return width
     */
    int width();

    /**
     * Height of the image in pixels
     *
     * @return height
     */
    int height();

    /**
     * Width of the image in blocks
     *
     * @return width in blocks
     */
    default float blockWidth() {
        return (float) width() / BLOCK_PIXELS;
    }

    /**
     * Height of the image in blocks
     *
     * @return height in blocks
     */
    default float blockHeight() {
        return (float) height() / BLOCK_PIXELS;
    }
}
