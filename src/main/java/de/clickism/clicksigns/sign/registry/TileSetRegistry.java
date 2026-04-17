package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.sign.Category;
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
    private static final Map<ResourceLocation, Category<TileSet>> CATEGORIES = new HashMap<>();

    /**
     * Registers a tile set with its resource location as its id.
     *
     * @param tileSet tile set to register
     */
    public static void registerTileSet(TileSet tileSet) {
        TILE_SETS.put(tileSet.location(), tileSet);
    }

    /**
     * Gets the tile set with the given resource location as its id.
     *
     * @param id resource location of the tile set to get
     * @return the tileset with the given id, or null if not found
     */
    public static @Nullable TileSet getTileSet(ResourceLocation id) {
        return TILE_SETS.get(id);
    }

    /**
     * Registers a tile set category with its resource location as its id.
     *
     * @param category category to register
     */
    public static void registerCategory(Category<TileSet> category) {
        CATEGORIES.put(category.identifier(), category);
    }

    /**
     * Gets the category with the given resource location as its id.
     *
     * @param id resource location of the category to get
     * @return the category with the given id, or null if not found
     */
    public static @Nullable Category<TileSet> getCategory(ResourceLocation id) {
        return CATEGORIES.get(id);
    }

    /**
     * Clears all registered tile sets and categories.
     */
    public static void clear() {
        TILE_SETS.clear();
        CATEGORIES.clear();
    }

    /**
     * Gets a list of all registered tile sets.
     *
     * @return a list of all registered tile sets
     */
    public static List<TileSet> allTileSets() {
        return TILE_SETS.values().stream().toList();
    }

    /**
     * Gets a list of all registered tile set ids.
     *
     * @return a list of all registered tile set ids
     */
    public static List<ResourceLocation> allTileSetIds() {
        return TILE_SETS.keySet().stream().toList();
    }
}
