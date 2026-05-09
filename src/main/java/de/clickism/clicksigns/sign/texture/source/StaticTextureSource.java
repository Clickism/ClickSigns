package de.clickism.clicksigns.sign.texture.source;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.util.Size;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Static texture source that loads a texture from a given resource location and caches its dimensions for future use.
 *
 * @param location resource location of the texture image
 */
public record StaticTextureSource(
        ResourceLocation location
) implements TextureSource {
    /**
     * Cache for texture sizes
     */
    private static final Map<ResourceLocation, Size> SIZE_CACHE = new HashMap<>();
    /**
     * Type key
     */
    public static final String TYPE = "static";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public Texture resolve(ColorResolver colorResolver) {
        try {
            var size = loadSize(location);
            return new Texture(location, size.width(), size.height());
        } catch (Exception e) {
            // Error silently since called quite often
            return ERROR_TEXTURE;
        }
    }

    /**
     * Loads the size of the image from the given resource location.
     *
     * @param location the resource location of the texture image
     * @return the size of the image
     */
    private static Size loadSize(ResourceLocation location) throws IOException {
        // Check cache first
        if (SIZE_CACHE.containsKey(location)) {
            return SIZE_CACHE.get(location);
        }
        // Load image and get dimensions
        var minecraft = Minecraft.getInstance();
        var resourceManager = minecraft.getResourceManager();
        try (var stream = resourceManager.open(location); var image = NativeImage.read(stream)) {
            var size = new Size(image.getWidth(), image.getHeight());
            SIZE_CACHE.put(location, size); // Update cache
            return size;
        }
    }
}
