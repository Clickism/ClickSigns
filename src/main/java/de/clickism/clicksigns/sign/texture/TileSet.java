package de.clickism.clicksigns.sign.texture;

import de.clickism.clicksigns.sign.Category;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a tileset
 *
 * @param name          name of the tileset to display
 * @param location      resource location of the tileset texture
 * @param cornerSize    size of the corners in pixels
 * @param centerSize    size of the center area in pixels
 * @param isBack        whether this tileset is for the back of the sign
 * @param colorResolver color resolver for this tileset
 * @param categoryId    optional category id for this tileset, used for grouping tilesets in the sign editor
 */
public record TileSet(
        String name,
        ResourceLocation location,
        int cornerSize,
        int centerSize,
        ColorResolver colorResolver,
        boolean isBack,
        @Nullable ResourceLocation categoryId
) {
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

    /**
     * Resolves the category of this tile set, if it has one.
     *
     * @return the category of this tile set, or null if it has no category
     */
    public @Nullable Category<TileSet> resolveCategory() {
        if (categoryId == null) return null;
        return TileSetRegistry.getCategory(categoryId);
    }
}
