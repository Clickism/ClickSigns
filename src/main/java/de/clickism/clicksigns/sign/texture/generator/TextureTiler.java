package de.clickism.clicksigns.sign.texture.generator;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.sign.texture.TileSet;
import net.minecraft.client.renderer.texture.DynamicTexture;

public class TextureTiler extends CachedTextureGenerator {
    private final TileSet tileSet;
    private final int width;
    private final int height;

    public TextureTiler(TileSet tileSet, int width, int height) {
        this.tileSet = tileSet;
        this.width = width;
        this.height = height;
    }

    @Override
    public DynamicTexture generate() throws Exception {
        var image = openImage(tileSet.location());
        assertCorrectSize(image, tileSet.cornerSize(), tileSet.centerSize());

        var tiledImage = new NativeImage(width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Get tiled pixel
                int pixel = image.getPixelRGBA(
                        tileSet.tileCoordinate(x, width),
                        tileSet.tileCoordinate(y, height)
                );
                tiledImage.setPixelRGBA(x, y, pixel);
            }
        }

        return new DynamicTexture(tiledImage);
    }

    @Override
    public String key() {
        return keySafe(tileSet.location()) + "_" + width + "x" + height;
    }

    /**
     * Asserts that a given image has the correct dimensions for the provided corner and center sizes.
     *
     * @param image      the image to check
     * @param cornerSize the size of the corner tiles in pixels
     * @param centerSize the size of the center tile in pixels
     */
    private static void assertCorrectSize(NativeImage image, int cornerSize, int centerSize) {
        int totalSize = 2 * cornerSize + centerSize;
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        if (imageWidth != totalSize || imageHeight != totalSize) {
            throw new IllegalArgumentException(
                    "TileSet image does not match expected dimensions! " +
                    "Expected: " + totalSize + "x" + totalSize +
                    ", but got: " + imageWidth + "x" + imageHeight
            );
        }
    }
}
