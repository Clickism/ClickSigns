package de.clickism.clicksigns.sign.texture.source;

import java.util.Arrays;

/**
 * Represents an image with its dimensions and pixel data.
 *
 * @param width  the width of the image in pixels
 * @param height the height of the image in pixels
 * @param pixels the pixel data of the image, stored as a one-dimensional array of ARGB integers
 */
public record Image(
    int width,
    int height,
    int[] pixels
) {
    /**
     * Creates a new Image with the specified width and height, initializing the pixel data to an empty array.
     *
     * @param width  the width of the image in pixels
     * @param height the height of the image in pixels
     */
    public Image(int width, int height) {
        this(width, height, new int[width * height]);
    }

    /**
     * Returns the color of the pixel at the specified (x, y) coordinates.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @return the color of the pixel as an ARGB integer
     * @throws IndexOutOfBoundsException if the (x, y) coordinates are out of bounds for this image
     */
    public int pixelAt(int x, int y) {
        if (!withinBounds(x, y)) {
            throw new IndexOutOfBoundsException(
                "Pixel coordinates out of bounds: (" + x + ", " + y + ") for image of size " + width + "x" + height
            );
        }
        return pixels[y * width + x];
    }

    /**
     * Sets the color of the pixel at the specified (x, y) coordinates.
     *
     * @param x     the x-coordinate of the pixel
     * @param y     the y-coordinate of the pixel
     * @param color the color to set the pixel to, as an ARGB integer
     * @throws IndexOutOfBoundsException if the (x, y) coordinates are out of bounds for this image
     */
    public void setPixelAt(int x, int y, int color) {
        if (!withinBounds(x, y)) {
            throw new IndexOutOfBoundsException(
                "Pixel coordinates out of bounds: (" + x + ", " + y + ") for image of size " + width + "x" + height
            );
        }
        pixels[y * width + x] = color;
    }

    /**
     * Checks if the specified (x, y) coordinates are within the bounds of this image.
     *
     * @param x the x-coordinate to check
     * @param y the y-coordinate to check
     * @return true if the coordinates are within bounds, false otherwise
     */
    public boolean withinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Iterates over each pixel in the image and applies the given PixelConsumer to it.
     *
     * @param consumer the PixelConsumer to apply to each pixel
     */
    public void forEachPixel(PixelConsumer consumer) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = pixelAt(x, y);
                consumer.accept(x, y, color);
            }
        }
    }

    /**
     * Fills the entire image with the specified color.
     *
     * @param color the color to fill the image with, as an ARGB integer
     */
    public void fill(int color) {
        Arrays.fill(pixels, color);
    }

    /**
     * Creates a copy of this Image, including its pixel data.
     *
     * @return a new Image instance with the same dimensions and pixel data
     */
    public Image copy() {
        return new Image(width, height, pixels.clone());
    }

    @FunctionalInterface
    public interface PixelConsumer {
        void accept(int x, int y, int color);
    }
}
