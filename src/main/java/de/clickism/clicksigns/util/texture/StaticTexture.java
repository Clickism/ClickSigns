package de.clickism.clicksigns.util.texture;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Texture record to wrap a texture with its dimensions.
 */
public record StaticTexture(ResourceLocation location, int width, int height) implements Texture {
    /**
     * Cache for texture sizes
     */
    private static final Map<ResourceLocation, TextureSize> SIZE_CACHE = new HashMap<>();

    /**
     * Loads a texture and its dimensions from the given resource location.
     * Will cache the dimensions of the texture.
     *
     * @param location the resource location of the texture image
     * @return the Texture
     */
    public static StaticTexture load(ResourceLocation location) {
        var size = loadSize(location);
        return new StaticTexture(location, size.width(), size.height());
    }

    /**
     * Loads the size of the image from the given resource location.
     *
     * @param location the resource location of the texture image
     * @return the size of the image
     */
    private static TextureSize loadSize(ResourceLocation location) {
        // Check cache first
        if (SIZE_CACHE.containsKey(location)) {
            return SIZE_CACHE.get(location);
        }
        // Load image and get dimensions
        try (var stream = Minecraft.getInstance().getResourceManager().open(location); var image = NativeImage.read(stream)) {
            var size = new TextureSize(image.getWidth(), image.getHeight());
            SIZE_CACHE.put(location, size); // Update cache
            return size;
        } catch (IOException e) {
            ClickSigns.LOGGER.error("Failed to load texture size for {}", location, e);
            return new TextureSize(0, 0);
        }
    }

    /**
     * Represents the size of a texture
     *
     * @param width  width of the texture in pixels
     * @param height height of the texture in pixels
     */
    private record TextureSize(int width, int height) {

    }
}
