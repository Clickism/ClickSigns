package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.util.texture.TileSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class TileSetRegistry {
    private static final Map<ResourceLocation, TileSet> TILE_SETS = new HashMap<>();

    public static void registerTileSet(ResourceLocation id, TileSet tileSet) {
        if (TILE_SETS.containsKey(id)) {
            throw new IllegalArgumentException("TileSet with id " + id + " is already registered");
        }
        TILE_SETS.put(id, tileSet);
    }

    public static TileSet get(ResourceLocation id) {
        if (!TILE_SETS.containsKey(id)) {
            throw new IllegalArgumentException("TileSet with id " + id + " is not registered");
        }
        return TILE_SETS.get(id);
    }
}
