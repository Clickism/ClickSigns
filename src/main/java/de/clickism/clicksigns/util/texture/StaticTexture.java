package de.clickism.clicksigns.util.texture;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A static texture
 */
public class StaticTexture implements Texture {
    private static final Map<ResourceLocation, TextureSize> SIZE_CACHE = new HashMap<>();

    private final ResourceLocation location;
    private final int width;
    private final int height;

    protected StaticTexture(ResourceLocation location, int width, int height) {
        this.location = location;
        this.width = width;
        this.height = height;
    }

    /**
     * Loads a static texture from the given resource location.
     *
     * @param location the resource location of the texture image
     * @return the StaticTexture
     */
    public static StaticTexture load(ResourceLocation location) {
        var size = loadSize(location);
        return new StaticTexture(location, size.width(), size.height());
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
        // No methods
    }
}
