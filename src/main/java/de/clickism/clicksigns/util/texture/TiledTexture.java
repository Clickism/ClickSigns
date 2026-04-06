package de.clickism.clicksigns.util.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

public class TiledTexture implements Texture {
    private static final Map<TiledTextureKey, ResourceLocation> GENERATED_CACHE = new HashMap<>();

    private final ResourceLocation location;
    private final int width;
    private final int height;

    protected TiledTexture(ResourceLocation location, int width, int height) {
        this.location = location;
        this.width = width;
        this.height = height;
    }

    /**
     * Generates a texture from the given tileset and block dimensions.
     * Caches textures based on the tileset and dimensions.
     *
     * @param tileSet     the tileset to generate the texture from
     * @param blockWidth  the width of the texture in blocks
     * @param blockHeight the height of the texture in blocks
     * @return TiledTexture instance
     */
    public static TiledTexture fromTileSet(TileSet tileSet, float blockWidth, float blockHeight) {
        var location = loadTexture(tileSet, blockWidth, blockHeight);
        return new TiledTexture(location, (int) (blockWidth * BLOCK_PIXELS), (int) (blockHeight * BLOCK_PIXELS));
    }

    /**
     * Loads a texture generated from the given tileset and block dimensions.
     */
    private static ResourceLocation loadTexture(TileSet tileSet, float blockWidth, float blockHeight) {
        var location = tileSet.location();
        var key = new TiledTextureKey(location, blockWidth, blockHeight);
        // Check cache first
        if (GENERATED_CACHE.containsKey(key)) {
            return GENERATED_CACHE.get(key);
        }
        // Generate texture and cache it
        var generated = tileSet.generate(blockWidth, blockHeight);
        var textureManager = Minecraft.getInstance().getTextureManager();
        var generatedLocation = textureManager.register(key.toString(), generated);
        GENERATED_CACHE.put(key, generatedLocation);
        return generatedLocation;
    }


    @Override
    public ResourceLocation location() {
        return this.location;
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
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
