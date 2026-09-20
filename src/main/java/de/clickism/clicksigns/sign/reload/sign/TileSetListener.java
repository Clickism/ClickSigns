package de.clickism.clicksigns.sign.reload.sign;

import com.google.gson.annotations.SerializedName;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.TileSet;
import de.clickism.clicksigns.sign.reload.DefinedTextureListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

/**
 * Tile set reload listener.
 */
public class TileSetListener extends DefinedTextureListener<TileSetListener.TileSetDefinition, TileSetListener.CategoryJson> {

    public static final String TILESET_EXTENSION = ".tileset.json";
    private static final String TILESET_DIRECTORY = "tilesets";

    /**
     * Creates a new tile set listener.
     */
    public TileSetListener() {
        super(SignRegistries.TILE_SETS, TILESET_DIRECTORY, TILESET_EXTENSION, CategoryJson.class, TileSetDefinition.class);
    }

    @Override
    public void onReload(ResourceManager manager) {
        SignRegistries.TILE_SET_COLOR_RESOLVERS.clear();
        super.onReload(manager);
    }

    @Override
    protected String categoryName(CategoryJson category) {
        return category.name();
    }

    @Override
    protected int priority(CategoryJson category) {
        return category.priority();
    }

    @Override
    protected void processImage(
        ResourceLocation location,
        Resource resource,
        @Nullable TileSetDefinition definition,
        @Nullable ResourceLocation categoryId,
        @Nullable TileSetListener.CategoryJson category
    ) {
        if (definition == null) {
            throw new IllegalArgumentException("No " + TILESET_EXTENSION + " or category definition found for tile set: " + location);
        }
        if (definition.colors != null) {
            var resolver = definition.colors.toColorResolver();
            SignRegistries.TILE_SET_COLOR_RESOLVERS.put(location, resolver);
        }
        SignRegistries.TILE_SETS.register(definition.toTileSet(location, categoryId));
    }

    /**
     * Tile set json format for tile set definitions.
     *
     * @param cornerSize size of the corner tiles in pixels
     */
    public record TileSetDefinition(
        int cornerSize,
        @Nullable ColorDefinition colors
    ) {
        TileSet toTileSet(ResourceLocation location, @Nullable ResourceLocation categoryId) {
            return new TileSet(
                location,
                categoryId,
                cornerSize
            );
        }
    }

    /**
     * Category JSON format for tileset category definitions.
     *
     * @param name name of the category
     * @param priority priority of the category
     */
    protected record CategoryJson(
        String name,
        int priority,
        @SerializedName("default")
        @Nullable TileSetDefinition defaultDefinition
    ) implements CategoryWithDefault<TileSetDefinition> {
    }
}
