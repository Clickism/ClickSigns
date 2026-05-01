package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.TileSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Tile set reload listener.
 */
public class TileSetListener extends CategorizedReloadListener<TileSetListener.CategoryJson> {
    private static final String TILESET_EXTENSION = ".tileset.json";
    private static final String TILESET_DIRECTORY = "tilesets";

    /**
     * Creates a new tile set listener.
     */
    public TileSetListener() {
        super(SignRegistries.TILE_SETS, TILESET_DIRECTORY, TILESET_EXTENSION, CategoryJson.class);
    }

    @Override
    protected String categoryName(CategoryJson category) {
        return category.name();
    }

    @Override
    protected void processResource(
            ResourceLocation location,
            Resource resource,
            @Nullable ResourceLocation categoryId,
            @Nullable CategoryJson category
    ) {
        var textureLocation = replaceExtension(location, TILESET_EXTENSION, ".png");
        var tileSetJson = fromJsonOrThrow(resource, TileSetJson.class);
        var isBack = category != null && category.isBack != null && category.isBack;
        SignRegistries.TILE_SETS.register(tileSetJson.toTileSet(textureLocation, isBack, categoryId));
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
    protected record CategoryJson(
            String name,
            @Nullable Boolean isBack
    ) {
    }
}
