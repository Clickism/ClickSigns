package de.clickism.clicksigns.sign.texture.generator;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
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
            // Calculate primary color from the center pixel of the texture
            int pixel = pixels.getPixelRGBA(pixels.getWidth() / 2, pixels.getHeight() / 2);
            // Convert RGBA to ARGB
            int argb = FastColor.ARGB32.color(
                FastColor.ABGR32.alpha(pixel),
                FastColor.ABGR32.red(pixel),
                FastColor.ABGR32.green(pixel),
                FastColor.ABGR32.blue(pixel)
            );
            return new Texture(location, pixels.getWidth(), pixels.getHeight(), argb);
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
     * Normalizes a resource location to be used as a cache key by replacing colons with double underscores.
     *
     * @param location the resource location to normalize
     * @return the normalized string representation of the resource location
     */
    protected String keySafe(ResourceLocation location) {
        return location.toString().replace(":", "__");
    }
}
