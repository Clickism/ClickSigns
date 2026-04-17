package de.clickism.clicksigns.sign.texture.generator;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class CachedTextureGenerator {
    private static final Map<String, Texture> TEXTURE_CACHE = new HashMap<>();

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

    protected DynamicTexture generateOrNull() {
        try {
            return generate();
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to generate texture with key {}", key(), e);
            return null;
        }
    }

    protected abstract DynamicTexture generate() throws Exception;

    protected abstract String key();

    protected static NativeImage openImage(ResourceLocation location) throws Exception {
        var minecraft = Minecraft.getInstance();
        try {
            return NativeImage.read(minecraft.getResourceManager().open(location));
        } catch (Exception ignored) {
        }
        var texture = minecraft.getTextureManager().getTexture(location);
        if (texture instanceof DynamicTexture dynamic) {
            return dynamic.getPixels();
        }
        return NativeImage.read(Minecraft.getInstance().getResourceManager().open(location));
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
