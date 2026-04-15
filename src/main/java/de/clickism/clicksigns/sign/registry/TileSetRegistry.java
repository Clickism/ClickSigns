package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.sign.texture.TileSet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for tile sets.
 */
public class TileSetRegistry {
    private static final Map<ResourceLocation, TileSet> TILE_SETS = new HashMap<>();

    /**
     * Registers a tile set with its resource location as its id.
     *
     * @param tileSet tile set to register
     */
    public static void register(TileSet tileSet) {
        TILE_SETS.put(tileSet.location(), tileSet);
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

    /**
     * Clears all registered tile sets.
     */
    public static void clear() {
        TILE_SETS.clear();
    }

    /**
     * Gets a list of all registered tile sets.
     *
     * @return a list of all registered tile sets
     */
    public static List<TileSet> all() {
        return TILE_SETS.values().stream().toList();
    }

    /**
     * Gets a list of all registered tile set ids.
     *
     * @return a list of all registered tile set ids
     */
    public static List<ResourceLocation> allIds() {
        return TILE_SETS.keySet().stream().toList();
    }
}
