package de.clickism.clicksigns.sign.texture.generator;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for generating and caching textures.
 */
public abstract class CachedTextureGenerator {
    private static final Map<String, Texture> TEXTURE_CACHE = new HashMap<>();

    /**
     * Generates a texture if it is not already cached and returns it.
     * If the generation fails, returns null and logs an error.
     *
     * @return the generated Texture, or null if generation failed
     */
    public @Nullable Texture getOrGenerate() {
        var key = this.key();
        return TEXTURE_CACHE.computeIfAbsent(key, (k) -> {
            var texture = generateOrNull();
            if (texture == null) return null;
            var textureManager = Minecraft.getInstance().getTextureManager();
            var location = textureManager.register(k, texture);
            var pixels = texture.getPixels();
            if (pixels == null) {
                ClickSigns.LOGGER.error("Failed to get pixels for generated texture with key {}", k);
                return null;
            }
            return new Texture(location, pixels.getWidth(), pixels.getHeight());
        });
    }

    /**
     * Calls generate, catches any errors and logs them to the console.
     *
     * @return generated texture, or null if generation failed
     */
    protected DynamicTexture generateOrNull() {
        try {
            return generate();
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to generate texture with key {}", key(), e);
            return null;
        }
    }

    /**
     * Generates a texture.
     *
     * @return the generated texture
     * @throws Exception if the generation fails for any reason
     */
    protected abstract DynamicTexture generate() throws Exception;

    /**
     * Returns a unique key for this texture generator, used for caching purposes.
     *
     * @return a unique string key representing this texture generator
     */
    protected abstract String key();

    /**
     * Opens an image from the given resource location, trying multiple methods to find it.
     *
     * @param location the resource location of the image to open
     * @return a NativeImage representing the opened image
     * @throws Exception if the image cannot be found or read using any of the methods
     */
    protected static NativeImage openImage(ResourceLocation location) throws Exception {
        // Try resource manager
        var minecraft = Minecraft.getInstance();
        try {
            return NativeImage.read(minecraft.getResourceManager().open(location));
        } catch (Exception ignored) {
        }
        // Try dynamic texture
        var texture = minecraft.getTextureManager().getTexture(location);
        if (texture instanceof DynamicTexture dynamic) {
            return dynamic.getPixels();
        }
        throw new IllegalArgumentException("Failed to open image at location " + location);
    }

    /**
     * Normalizes a resource location to be used as a cache key by replacing colons with double underscores.
     *
     * @param location the resource location to normalize
     * @return the normalized string representation of the resource location
     */
    protected String keySafe(ResourceLocation location) {
        return location.toString().replace(":", "__");
    }
}
