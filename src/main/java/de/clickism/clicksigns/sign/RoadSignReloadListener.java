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
        ).forEach((location, resource) -> {
            try {
                // Important! Image path and tileset path must be the same!
                var texturePath = location.getPath().replace(".tileset.json", ".png");
                var textureLocation = ResourceLocation.tryBuild(location.getNamespace(), texturePath);
                var tileSet = GSON.fromJson(resource.openAsReader(), TileSetJson.class).toTileSet(textureLocation);
                TileSetRegistry.register(tileSet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private record TileSetJson(
            String name,
            int cornerSize,
            int centerSize
    ) {
        TileSet toTileSet(ResourceLocation location) {
            return new TileSet(
                    name,
                    location,
                    cornerSize,
                    centerSize
            );
        }
    }
}
