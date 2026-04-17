package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.sign.Category;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.sign.ColorResolver;
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
        var categories = loadAndRegisterCategories(manager, "tilesets", CategoryJson.class, (identifier, json) -> {
            var category = Category.forTileSet(identifier, json.name());
            TileSetRegistry.registerCategory(category);
        });
        manager.listResources(
                fromRoot("tilesets"),
                identifier -> identifier.getPath().endsWith(".tileset.json")
        ).forEach((location, resource) -> {
            // Important! Image path and tileset path must be the same!
            var path = location.getPath();
            var directory = stripFileName(path);
            var categoryId = ResourceLocation.tryBuild(location.getNamespace(), directory);
            var category = categories.get(categoryId);
            if (category == null) {
                categoryId = null; // No category
            }
            var texturePath = path.replace(".tileset.json", ".png");
            var textureLocation = ResourceLocation.tryBuild(location.getNamespace(), texturePath);
            var tileSetJson = fromJsonOrNull(resource, TileSetJson.class);
            if (tileSetJson == null) return;
            // Check if category has isBack set to true
            var isBack = category != null && category.isBack != null && category.isBack;
            TileSetRegistry.registerTileSet(tileSetJson.toTileSet(textureLocation, isBack, categoryId));
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
        TileSet toTileSet(ResourceLocation location, boolean isBack, @Nullable ResourceLocation categoryId) {
            var resolver = ColorResolver.withDefault();
            if (colors != null) {
                colors.forEach(resolver::tryParseAndDefine);
            }
            return new TileSet(
                    name,
                    location,
                    cornerSize,
                    centerSize,
                    resolver,
                    isBack,
                    categoryId
            );
        }
    }

    /**
     * Category JSON format for tileset category definitions.
     *
     * @param name name of the category
     */
    private record CategoryJson(
            String name,
            @Nullable Boolean isBack
    ) {
    }
}
