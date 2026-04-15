package de.clickism.clicksigns.sign.texture;

import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import net.minecraft.resources.ResourceLocation;

/**
 * Represents a tileset
 *
 * @param name          name of the tileset to display
 * @param location      resource location of the tileset texture
 * @param cornerSize    size of the corners in pixels
 * @param centerSize    size of the center area in pixels
 * @param colorResolver color resolver for this tileset
 */
public record TileSet(
        String name,
        ResourceLocation location,
        int cornerSize,
        int centerSize,
        ColorResolver colorResolver
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
}
