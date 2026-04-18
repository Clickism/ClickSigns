package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.Identifiable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a tileset
 *
 * @param name          name of the tileset to display
 * @param identifier    the identifier and resource location of the tileset texture
 * @param categoryId    optional category id for this tileset, used for grouping tilesets in the sign editor
 * @param cornerSize    size of the corners in pixels
 * @param centerSize    size of the center area in pixels
 * @param colorResolver color resolver for this tileset
 * @param isBack        whether this tileset is for the back of the sign
 */
public record TileSet(
        String name,
        ResourceLocation identifier,
        @Nullable ResourceLocation categoryId,
        int cornerSize,
        int centerSize,
        ColorResolver colorResolver,
        boolean isBack
) implements Identifiable, Categorized<TileSet> {
    /**
     * Tiles a given coordinate:
     * - If coordinate is within the first edge, do nothing
     * - If coordinate is within the center area, wrap the coord around the center texture
     * - If coordinate is iwthin the second edge, wrap the coord around the second edge texture
     *
     * @param coord     the coordinate to tile
     * @param totalSize total size of the tiled texture in the given dimension
     * @return the tiled coordinate
     */
    public int tileCoordinate(int coord, int totalSize) {
        int centerStart = cornerSize;
        int centerEnd = totalSize - cornerSize;
        if (coord >= centerStart && coord < centerEnd) {
            // Inside center
            int local = (coord - centerStart) % centerSize;
            return local + centerStart;
        } else if (coord >= centerEnd) {
            // Inside right/bottom edge
            int local = coord - centerEnd;
            return local + centerStart + centerSize;
        }
        return coord;
    }

    @Override
    public CategorizedRegistry<TileSet> registry() {
        return SignRegistries.TILE_SETS;
    }
}
