package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.util.texture.TileSet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for tile sets.
 */
public class TileSetRegistry {
    private static final Map<ResourceLocation, TileSet> TILE_SETS = new HashMap<>();

    /**
     * Registers a tile set with the given resource location as its id.
     *
     * @param id      id of the tile set to register
     * @param tileSet tile set to register
     */
    public static void register(ResourceLocation id, TileSet tileSet) {
        TILE_SETS.put(id, tileSet);
    }

    /**
     * Gets the tile set with the given resource location as its id.
     *
     * @param id resource location of the tile set to get
     * @return the tileset with the given id, or null if not found
     */
    public static @Nullable TileSet get(ResourceLocation id) {
        return TILE_SETS.get(id);
    }
}
