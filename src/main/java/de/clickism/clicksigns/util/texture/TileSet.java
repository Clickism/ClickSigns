package de.clickism.clicksigns.util.texture;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Represents a tileset image
 */
public record TileSet(
        ResourceLocation location,
        int cornerSize,
        int centerSize
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
