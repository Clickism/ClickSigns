package de.clickism.clicksigns.sign;

import com.google.gson.Gson;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.util.texture.TileSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reload listener for road sign related data
 */
public class RoadSignReloadListener implements Platform.ReloadListener {
    private static final Gson GSON = new Gson();

    @Override
    public void onReload(ResourceManager manager) {
        loadTileSets(manager);
        loadSymbols(manager);
    }

    /**
     * Loads all tile sets from the resource manager and registers them in the tile set registry.
     *
     * @param manager the resource manager to load the tile sets from
     */
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

    /**
     * Loads all symbols from the resource manager and registers them in the symbol registry.
     */
    private void loadSymbols(ResourceManager manager) {
        SymbolRegistry.clear();
        var directoryToCategory = loadCategories(manager);
        manager.listResources(
                "roadsigns/symbols",
                identifier -> identifier.getPath().endsWith(".png")
        ).forEach((location, resource) -> {
            var path = location.getPath();
            var directory = path.substring(0, path.lastIndexOf('/'));
            var category = directoryToCategory.getOrDefault(directory, new SymbolCategory(SymbolRegistry.UNCATEGORIZED, List.of()));
            SymbolRegistry.register(category.name(), location);

        });
        // Resolve included categories
        directoryToCategory.values().forEach(category -> {
            category.includedCategories().forEach(included -> {
                var includedSymbols = SymbolRegistry.allInCategory(included);
                includedSymbols.forEach(symbol -> SymbolRegistry.addToCategory(category.name(), symbol));
            });
        });
    }

    /**
     * Loads all symbol categories from the resource manager and returns a map of directory to category name.
     *
     * @return a map of directory to category
     */
    private Map<String, SymbolCategory> loadCategories(ResourceManager manager) {
        // Map from directory to category name
        Map<String, SymbolCategory> directoryToCategory = new HashMap<>();
        manager.listResources(
                "roadsigns/symbols",
                identifier -> identifier.getPath().endsWith("category.json")
        ).forEach((location, resource) -> {
            try {
                var directory = location.getPath().replace("/category.json", "");
                var category = GSON.fromJson(resource.openAsReader(), CategoryJson.class).toSymbolCategory();
                directoryToCategory.put(directory, category);
            } catch (IOException e) {
                ClickSigns.LOGGER.error("Error occurred while loading symbol category json {}", location, e);
            }
        });
        return directoryToCategory;
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

    /**
     * Category JSON format for symbol categories.
     * Important: The category JSON will assign its category to all symbols in the same directory as the JSON file.
     *
     * @param name    name of the category
     * @param include included list of other categories
     */
    private record CategoryJson(
            String name,
            @Nullable List<String> include
    ) {
        SymbolCategory toSymbolCategory() {
            return new SymbolCategory(name, include != null ? include : List.of());
        }
    }

    /**
     * Record to represent a symbol category with its included categories.
     *
     * @param name               name of the category
     * @param includedCategories list of included categories
     */
    private record SymbolCategory(
            String name,
            List<String> includedCategories
    ) {
    }
}
