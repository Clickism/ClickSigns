package de.clickism.clicksigns.sign.texture.generator;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.sign.texture.TileSet;
import net.minecraft.client.renderer.texture.DynamicTexture;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

public class TextureTiler extends CachedTextureGenerator {
    private final TileSet tileSet;
    private final float blockWidth;
    private final float blockHeight;

    public TextureTiler(TileSet tileSet, float blockWidth, float blockHeight) {
        this.tileSet = tileSet;
        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;
    }

    @Override
    public DynamicTexture generate() throws Exception {
        var image = openImage(tileSet.location());
        assertCorrectSize(image, tileSet.cornerSize(), tileSet.centerSize());

        int tiledWidth = (int) (blockWidth * BLOCK_PIXELS);
        int tiledHeight = (int) (blockHeight * BLOCK_PIXELS);
        var tiledImage = new NativeImage(tiledWidth, tiledHeight, false);

        for (int y = 0; y < tiledHeight; y++) {
            for (int x = 0; x < tiledWidth; x++) {
                // Get tiled pixel
                int pixel = image.getPixelRGBA(
                        tileSet.tileCoordinate(x, tiledWidth),
                        tileSet.tileCoordinate(y, tiledHeight)
                );
                tiledImage.setPixelRGBA(x, y, pixel);
            }
        }

        return new DynamicTexture(tiledImage);
    }

    @Override
    public String key() {
        return keySafe(tileSet.location()) + "_" + blockWidth + "x" + blockHeight;
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
