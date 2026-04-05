package de.clickism.clicksigns.render;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/**
 * Represents a tileset image
 */
public class TileSet {
    private static final int BLOCK_PIXELS = 16;

    private final NativeImage image;
    private final int cornerSize;
    private final int centerSize;

    /**
     * Loads a tileset from the given resource location.
     * The image should be a square with width (2 * cornerSize + centerSize).
     *
     * @param texture    the resource location of the tileset image
     * @param cornerSize the width of the corner tiles in pixels (e.g. 4)
     * @param centerSize the width of the center tile in pixels (e.g. 8)
     * @throws IOException if the image cannot be loaded
     */
    public TileSet(ResourceLocation texture, int cornerSize, int centerSize) throws IOException {
        this.image = NativeImage.read(Minecraft.getInstance().getResourceManager().open(texture));
        this.cornerSize = cornerSize;
        this.centerSize = centerSize;
        int totalSize = 2 * cornerSize + centerSize;
        // Check if dimensions match
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

    /**
     * Generates a tiled texture of the given dimensions from this tileset.
     */
    public DynamicTexture generate(int blockWidth, int blockHeight) {
        int imageWidth = blockWidth * BLOCK_PIXELS;
        int imageHeight = blockHeight * BLOCK_PIXELS;
        var tiledImage = new NativeImage(imageWidth, imageHeight, false);

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                int pixel = this.pixelAt(x, y, imageWidth, imageHeight);
                tiledImage.setPixelRGBA(x, y, pixel);
            }
        }

        return new DynamicTexture(tiledImage);
    }

    /**
     * Gets the tiled pixel at a given coordinate
     */
    private int pixelAt(int x, int y, int imageWidth, int imageHeight) {
        return image.getPixelRGBA(
                tileCoordinate(x, imageWidth),
                tileCoordinate(y, imageHeight)
        );
    }

    /**
     * Tiles a given coordinate:
     * - If coordinate is within the first edge, do nothing
     * - If coordinate is within the center area, wrap the coord around the center texture
     * - If coordinate is iwthin the second edge, wrap the coord around the second edge texture
     */
    private int tileCoordinate(int coord, int totalSize) {
        int centerStart = cornerSize;
        int centerEnd = totalSize - cornerSize;
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
