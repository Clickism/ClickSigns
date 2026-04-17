package de.clickism.clicksigns.sign.texture;

import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import net.minecraft.resources.ResourceLocation;

/**
 * Texture interface to represent a texture with its resource location and dimensions.
 *
 * @param location resource location of the texture image
 * @param width    width of the texture image in pixels
 * @param height   height of the texture image in pixels
 */
public record Texture(
        ResourceLocation location,
        int width,
        int height
) implements PixelSized {
    /**
     * Loads a static texture from the given resource location and returns it as a Texture object.
     *
     * @param location the resource location of the texture image to load
     * @return the loaded Texture object
     */
    public static Texture loadStatic(ResourceLocation location) {
        return new StaticTextureSource(location).resolve();
    }
}
