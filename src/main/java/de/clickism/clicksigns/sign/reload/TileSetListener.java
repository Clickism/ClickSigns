package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import de.clickism.clicksigns.sign.texture.TileSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Tile set reload listener.
 */
public class TileSetListener implements RoadSignReloadListener {
    @Override
    public void onReload(ResourceManager manager) {
        TileSetRegistry.clear();
        manager.listResources(
                fromRoot("tilesets"),
                identifier -> identifier.getPath().endsWith(".tileset.json")
        ).forEach((location, resource) -> {
            // Important! Image path and tileset path must be the same!
            var texturePath = location.getPath().replace(".tileset.json", ".png");
            var textureLocation = ResourceLocation.tryBuild(location.getNamespace(), texturePath);
            var tileSetJson = fromJsonOrNull(resource, TileSetJson.class);
            if (tileSetJson == null) return;
            TileSetRegistry.register(tileSetJson.toTileSet(textureLocation));
        });
    }

    /**
     * Tile set json format for tile set definitions.
     *
     * @param name       name of the tile set
     * @param cornerSize size of the corner tiles in pixels
     * @param centerSize size of the center tiles in pixels
     */
    private record TileSetJson(
            String name,
            int cornerSize,
            int centerSize,
            @Nullable Map<String, String> colors
    ) {
        TileSet toTileSet(ResourceLocation location) {
            var resolver = ColorResolver.withDefault();
            if (colors != null) {
                colors.forEach(resolver::tryParseAndDefine);
            }
            return new TileSet(
                    name,
                    location,
                    cornerSize,
                    centerSize,
                    resolver
            );
        }
    }
}
