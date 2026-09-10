package de.clickism.clicksigns.sign.texture;

import de.clickism.clicksigns.util.PixelSized;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Texture interface to represent a texture with its resource location and dimensions.
 *
 * @param location     resource location of the texture image
 * @param width        width of the texture image in pixels
 * @param height       height of the texture image in pixels
 * @param primaryColor optional primary color of the texture, if applicable, represented as an ARGB integer
 */
public record Texture(
    ResourceLocation location,
    int width,
    int height,
    @Nullable Integer primaryColor
) implements PixelSized {
}
