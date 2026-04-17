package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.TileSet;
import de.clickism.clicksigns.sign.texture.generator.TextureTiler;
import de.clickism.clicksigns.sign.texture.PixelSized;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Texture source that generates a texture by tiling a given tileset to fit the specified dimensions.
 *
 * @param tileSetId resource location of the tileset to use for tiling
 * @param width     width of the generated texture in pixels
 * @param height    height of the generated texture in pixels
 */
public record TiledTextureSource(
        ResourceLocation tileSetId,
        int width,
        int height
) implements TextureSource, PixelSized {
    /**
     * Type key
     */
    public static final String TYPE = "tiled";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public Texture resolve(ColorResolver colorResolver) {
        var tileSet = resolveTileSet();
        if (tileSet == null) return ERROR_TEXTURE;
        var texture = new TextureTiler(tileSet, width, height).getOrGenerate();
        if (texture == null) return ERROR_TEXTURE;
        return texture;
    }

    /**
     * Resolves the tileset for this texture source.
     *
     * @return the resolved tileset, or null if not found
     */
    public @Nullable TileSet resolveTileSet() {
        return TileSetRegistry.get(tileSetId);
    }
}
