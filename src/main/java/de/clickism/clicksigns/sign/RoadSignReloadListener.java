package de.clickism.clicksigns.sign;

import com.google.gson.Gson;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.util.texture.TileSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

/**
 * Reload listener for road sign related data
 */
public class RoadSignReloadListener implements Platform.ReloadListener {
    private static final Gson GSON = new Gson();

    @Override
    public void onReload(ResourceManager manager) {
        loadTileSets(manager);
    }

    private void loadTileSets(ResourceManager manager) {
        TileSetRegistry.clear();
        manager.listResources(
                "roadsigns/tilesets",
                identifier -> identifier.getPath().endsWith(".tileset.json")
        ).forEach((identifier, resource) -> {
            try {
                var tileSet = GSON.fromJson(resource.openAsReader(), TileSetJson.class).toTileSet();
                TileSetRegistry.register(tileSet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private record TileSetJson(
            String name,
            String texture,
            int cornerSize,
            int centerSize
    ) {
        TileSet toTileSet() {
            return new TileSet(
                    name,
                    ResourceLocation.tryParse(texture),
                    cornerSize,
                    centerSize
            );
        }
    }
}
