package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a tileset
 *
 * @param identifier    the identifier and resource location of the tileset texture
 * @param categoryId    optional category id for this tileset, used for grouping tilesets in the sign editor
 * @param cornerSize    size of the corners in pixels
 * @param centerSize    size of the center area in pixels
 * @param isBack        whether this tileset is for the back of the sign
 */
public record TileSet(
    ResourceLocation identifier,
    @Nullable ResourceLocation categoryId,
    int cornerSize,
    int centerSize,
    boolean isBack
) implements Categorized<TileSet> {
    @Override
    public CategorizedRegistry<TileSet> registry() {
        return SignRegistries.TILE_SETS;
    }
}
