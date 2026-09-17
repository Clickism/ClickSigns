package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a tileset
 *
 * @param identifier the identifier and resource location of the tileset texture
 * @param categoryId optional category id for this tileset, used for grouping tilesets in the sign editor
 * @param cornerSize size of the corners in pixels
 * @param isBack     whether this tileset is for the back of the sign
 */
public record TileSet(
    ResourceLocation identifier,
    @Nullable ResourceLocation categoryId,
    int cornerSize,
    boolean isBack
) implements Categorized<TileSet> {
    @Override
    public CategorizedRegistry<TileSet> registry() {
        return SignRegistries.TILE_SETS;
    }

    /**
     * Returns a texture source for this tileset, with the given width and height.
     *
     * @param width  the width of the texture in pixels
     * @param height the height of the texture in pixels
     * @return a texture source that provides the texture for this tileset
     */
    public TextureSource textureSource(int width, int height) {
        return new TextureSource(identifier, List.of(
            new Tiler(cornerSize, width, height)
        ));
    }
}
