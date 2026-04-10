package de.clickism.clicksigns.util.texture;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Texture interface to represent a texture with its resource location and dimensions.
 */
public sealed interface Texture permits StaticTexture, TiledTexture {
    /**
     * The error texture to use when loading or generating a texture fails.
     */
    ResourceLocation ERROR_TEXTURE = ClickSigns.identifier("error.png");

    /**
     * Resource location of the texture's image
     *
     * @return resource location
     */
    ResourceLocation location();

    /**
     * Width of the image in pixels
     *
     * @return width in pixels
     */
    int width();

    /**
     * Height of the image in pixels
     *
     * @return height in pixels
     */
    int height();

    /**
     * Width of the image in blocks
     *
     * @return width in blocks
     */
    default float blockWidth() {
        return (float) width() / BLOCK_PIXELS;
    }

    /**
     * Height of the image in blocks
     *
     * @return height in blocks
     */
    default float blockHeight() {
        return (float) height() / BLOCK_PIXELS;
    }

    /**
     * Loads a texture from the given resource location, including its dimensions.
     *
     * @param location the resource location of the texture image
     * @return the loaded texture
     */
    static StaticTexture load(ResourceLocation location) {
        return StaticTexture.load(location);
    }

    /**
     * Wraps a generated texture with the given dimensions and tileset information into a TiledTexture record.
     *
     * @param generated   the resource location of the generated texture
     * @param blockWidth  the width of the generated texture in blocks
     * @param blockHeight the height of the generated texture in blocks
     * @param tileSet     the resource location of the tileset used to generate the texture (for reference)
     * @return a TiledTexture record containing the resource location and dimensions of the generated texture
     */
    static TiledTexture wrapTiled(ResourceLocation generated, float blockWidth, float blockHeight, ResourceLocation tileSet) {
        int pixelWidth = (int) (blockWidth * BLOCK_PIXELS);
        int pixelHeight = (int) (blockHeight * BLOCK_PIXELS);
        return new TiledTexture(generated, pixelWidth, pixelHeight, tileSet);
    }

    FriendlyByteBuf.Writer<Texture> WRITER = (buf, texture) -> {
        int type = texture instanceof TiledTexture ? 1 : 0;
        buf.writeInt(type);
        if (texture instanceof TiledTexture tiled) {
            // Tiled texture
            buf.writeResourceLocation(tiled.tileSet());
            buf.writeFloat(tiled.blockWidth());
            buf.writeFloat(tiled.blockHeight());
        } else {
            // Static texture
            buf.writeResourceLocation(texture.location());
        }
    };

    FriendlyByteBuf.Reader<Texture> READER = (buf) -> {
        int type = buf.readInt();
        if (type == 1) {
            // Tiled texture
            var tileSetId = buf.readResourceLocation();
            var blockWidth = buf.readFloat();
            var blockHeight = buf.readFloat();
            // Generate the tiled texture
            var tileSet = TileSetRegistry.get(tileSetId);
            if (tileSet == null) {
                ClickSigns.LOGGER.error("Failed to load tileset {} for texture", tileSetId);
                return Texture.load(ERROR_TEXTURE);
            }
            var generated = TiledTextureGenerator.generateAndRegister(tileSet, blockWidth, blockHeight);
            if (generated == null) {
                ClickSigns.LOGGER.error("Failed to generate tiled texture for tileset {}", tileSetId);
                return Texture.load(ERROR_TEXTURE);
            }
            return Texture.wrapTiled(generated.location(), blockWidth, blockHeight, tileSetId);
        } else {
            // Static texture
            var location = buf.readResourceLocation();
            return Texture.load(location);
        }
    };
}
