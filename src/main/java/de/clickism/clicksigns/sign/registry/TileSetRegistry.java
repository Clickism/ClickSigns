package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.util.texture.TileSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class TileSetRegistry {
    private static final Map<ResourceLocation, TileSet> TILE_SETS = new HashMap<>();

    public static void registerTileSet(ResourceLocation id, TileSet tileSet) {
        TILE_SETS.put(id, tileSet);
    }

    public static @Nullable TileSet get(ResourceLocation id) {
        return TILE_SETS.get(id);
    }
}
