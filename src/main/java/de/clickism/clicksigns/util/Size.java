package de.clickism.clicksigns.util;

/**
 * Represents the size of an object (road sign/template/texture) in pixels.
 *
 * @param width  width in pixels
 * @param height height in pixels
 */
public record Size(
        int width,
        int height
) implements PixelSized {
}
