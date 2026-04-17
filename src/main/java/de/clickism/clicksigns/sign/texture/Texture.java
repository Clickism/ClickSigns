package de.clickism.clicksigns.sign.texture;

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
}
