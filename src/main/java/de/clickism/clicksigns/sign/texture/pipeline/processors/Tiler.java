package de.clickism.clicksigns.sign.texture.pipeline.processors;

import de.clickism.clicksigns.sign.texture.pipeline.Image;
import de.clickism.clicksigns.sign.texture.pipeline.ResizableTextureProcessor;
import de.clickism.clicksigns.sign.texture.pipeline.TextureContext;
import de.clickism.clicksigns.sign.texture.pipeline.TextureProcessor;

/**
 * Tiles a given image based on the specified tile set and dimensions.
 *
 * @param cornerSize   the size of the corner part in pixels
 * @param outputWidth  the width of the tiled image in pixels
 * @param outputHeight the height of the tiled image in pixels
 */
public record Tiler(
    int cornerSize,
    int outputWidth,
    int outputHeight
) implements ResizableTextureProcessor {
    @Override
    public Image process(Image input, TextureContext context) {
        var output = new Image(outputWidth, outputHeight);
        int inputWidth = input.width();
        int inputHeight = input.height();
        output.forEachPixel((x, y, empty) -> {
            int tiledX = tileCoordinate(x, inputWidth, outputWidth);
            int tiledY = tileCoordinate(y, inputHeight, outputHeight);
            int color = input.pixelAt(tiledX, tiledY);
            output.setPixelAt(x, y, color);
        });
        return output;
    }

    @Override
    public TextureProcessor resize(int width, int height) {
        return new Tiler(cornerSize, width, height);
    }

    @Override
    public String identity() {
        return this.toString();
    }

    /**
     * Calculates the tiled coordinate for a given pixel coordinate based on the corner size and total size.
     *
     * @param coord      the original pixel coordinate (x or y)
     * @param inputSize  the size of the input image (width or height)
     * @param outputSize the total size of the output image (width or height)
     * @return the tiled coordinate corresponding to the original coordinate
     */
    private int tileCoordinate(int coord, int inputSize, int outputSize) {
        // Calculate center size based on input size and corner size
        int centerSize = inputSize - 2 * cornerSize;
        // Calculate bounds
        int centerStart = cornerSize;
        int centerEnd = outputSize - cornerSize;
        if (coord >= centerStart && coord < centerEnd) {
            // Inside center
            int local = (coord - centerStart) % centerSize;
            return local + centerStart;
        } else if (coord >= centerEnd) {
            // Inside right/bottom edge
            int local = coord - centerEnd;
            return local + centerStart + centerSize;
        }
        return coord;
    }
}
