package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.TileSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Tile set reload listener.
 */
public class TileSetListener implements RoadSignReloadListener {

    private static final String TILESET_EXTENSION = ".tileset.json";

    @Override
    public void onReload(ResourceManager manager) {
        SignRegistries.TILE_SETS.clear();
        var categories = loadAndRegisterCategories(manager, "tilesets", CategoryJson.class, (identifier, json) -> {
            SignRegistries.TILE_SETS.createAndRegisterCategory(identifier, json.name());
        });
        forEachResource(manager, fromRoot("tilesets"), TILESET_EXTENSION, (location, resource) -> {
            // Important! Image path and tileset path must be the same!
            var categoryId = categoryIdOf(location);
            var category = categories.get(categoryId);
            if (category == null) {
                categoryId = null; // No category
            }
            var textureLocation = replaceExtension(location, TILESET_EXTENSION, ".png");
            var tileSetJson = fromJsonOrThrow(resource, TileSetJson.class);
            var isBack = category != null && category.isBack != null && category.isBack;
            SignRegistries.TILE_SETS.register(tileSetJson.toTileSet(textureLocation, isBack, categoryId));
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
                    categoryId, cornerSize,
                    centerSize,
                    resolver,
                    isBack
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
