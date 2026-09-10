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
    public Size withWidth(int newWidth) {
        return new Size(newWidth, this.height);
    }

    public Size withHeight(int newHeight) {
        return new Size(this.width, newHeight);
    }

    public Size clamped(Size minSize, Size maxSize) {
        int clampedWidth = Math.max(minSize.width(), Math.min(maxSize.width(), this.width));
        int clampedHeight = Math.max(minSize.height(), Math.min(maxSize.height(), this.height));
        return new Size(clampedWidth, clampedHeight);
    }
}
