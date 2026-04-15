package de.clickism.clicksigns.sign.texture;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Utility class for generating tiled textures from tilesets.
 */
public class TiledTextureGenerator {
    /**
     * Cache for generated textures
     */
    private static final Map<TiledTextureKey, ResourceLocation> GENERATED_CACHE = new HashMap<>();

    /**
     * Generates a tiled texture from the given tileset and block dimensions, registers it with the texture manager,
     * and returns a Texture record containing the resource location and dimensions of the generated texture.
     *
     * @param tileSet     the tileset to generate the texture from
     * @param blockWidth  the width of the generated texture in blocks
     * @param blockHeight the height of the generated texture in blocks
     * @return the generated texture or null if generation failed
     */
    public static @Nullable TiledTexture generate(TileSet tileSet, float blockWidth, float blockHeight) {
        var location = generateImageAndRegister(tileSet, blockWidth, blockHeight);
        if (location == null) return null;
        return Texture.wrapTiled(location, blockWidth, blockHeight, tileSet.location());
    }

    /**
     * Generates a tiled image from the given tileset and block dimensions, and registers it with the texture manager.
     */
    private static @Nullable ResourceLocation generateImageAndRegister(TileSet tileSet, float blockWidth, float blockHeight) {
        var key = new TiledTextureKey(tileSet.location(), blockWidth, blockHeight);
        // Check cache first
        if (GENERATED_CACHE.containsKey(key)) {
            return GENERATED_CACHE.get(key);
        }
        // Generate texture and cache it
        try {
            var dynamicTexture = generateImage(tileSet, blockWidth, blockHeight);
            var textureManager = Minecraft.getInstance().getTextureManager();
            var generatedLocation = textureManager.register(key.toString(), dynamicTexture);
            GENERATED_CACHE.put(key, generatedLocation);
            return generatedLocation;
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to generate tiled texture for tileset {}", tileSet.location(), e);
            return null;
        }
    }

    /**
     * Generates a tiled texture of the given dimensions from the given tileset.
     * Will load the tileset image and tile it accordingly.
     */
    private static DynamicTexture generateImage(TileSet tileSet, float blockWidth, float blockHeight)
            throws IOException, IllegalArgumentException {
        var image = NativeImage.read(Minecraft.getInstance().getResourceManager().open(tileSet.location()));
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

    /**
     * Key for caching generated textures
     */
    private record TiledTextureKey(ResourceLocation location, float blockWidth, float blockHeight) {
        @Override
        public @NotNull String toString() {
            // Replace ":" in resource location with "__" to avoid issues in texture names
            // Use block dimensions in the key to differentiate textures generated for different sizes
            return location.toString().replace(":", "__") + "_" + blockWidth + "x" + blockHeight;
        }
    }
}
