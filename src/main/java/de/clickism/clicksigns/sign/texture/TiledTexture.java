package de.clickism.clicksigns.sign.texture;

import net.minecraft.resources.ResourceLocation;

/**
 * Represents a texture that was generated using a tileset.
 *
 * @param location resource location of the generated texture
 * @param width    width of the generated texture in pixels
 * @param height   height of the generated texture in pixels
 * @param tileSet  resource location of the tileset used to generate the texture
 */
public record TiledTexture(
        ResourceLocation location,
        int width,
        int height,
        ResourceLocation tileSet
) implements Texture {

}
