package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.ReloadListener;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * Symbol reload listener.
 */
public class SymbolListener implements ReloadListener {
    @Override
    public void onReload(ResourceManager manager) {
        SymbolRegistry.clear();
        var directoryToCategory = loadCategories(manager);
        manager.listResources(
                "roadsigns/symbols",
                identifier -> identifier.getPath().endsWith(".png")
        ).forEach((location, resource) -> {
            var path = location.getPath();
            var directory = path.substring(0, path.lastIndexOf('/'));
            var category = directoryToCategory.get(directory);
            var categoryName = category != null ? category.name() : SymbolRegistry.UNCATEGORIZED;
            var symbol = new Symbol(location, new StaticTextureSource(location), categoryName);
            SymbolRegistry.registerSymbol(symbol);
        });
        resolveIncludedSymbols(directoryToCategory.values());
    }

    /**
     * Creates a new symbol identifier for a symbol included from another category, to avoid conflicts with the original symbol.
     *
     * @param location     original symbol identifier
     * @param categoryName name of the category to include the symbol in
     * @return a new resource location for the included symbol, based on the original location and the category name
     */
    private ResourceLocation idForCategory(ResourceLocation location, String categoryName) {
        var normalized = categoryName.toLowerCase().replaceAll("[^a-z0-9_.-]+", "_").toLowerCase();
        return ResourceLocation.tryBuild(location.getNamespace(), location.getPath() + "__" + normalized);
    }

    /**
     * Loads all symbol categories from the resource manager and returns a map of directory to category name.
     *
     * @return a map of directory to category
     */
    private Map<String, CategoryJson> loadCategories(ResourceManager manager) {
        // Map from directory to category name
        Map<String, CategoryJson> directoryToCategory = new HashMap<>();
        manager.listResources(
                "roadsigns/symbols",
                identifier -> identifier.getPath().endsWith("category.json")
        ).forEach((location, resource) -> {
            try {
                var directory = location.getPath().replace("/category.json", "");
                var category = GSON.fromJson(resource.openAsReader(), CategoryJson.class);
                directoryToCategory.put(directory, category);
            } catch (IOException e) {
                ClickSigns.LOGGER.error("Error occurred while loading symbol category json {}", location, e);
            }
        });
        return directoryToCategory;
    }

    /**
     * Resolves included symbols for all categories and registers them with modified identifiers to avoid conflicts.
     *
     * @param categories collection of categories to resolve included symbols for
     */
    private void resolveIncludedSymbols(Collection<CategoryJson> categories) {
        // Resolve included categories
        categories.forEach(category -> {
            if (category.includeCategories == null) return;
            category.includeCategories.forEach(includedName -> {
                var included = SymbolRegistry.getCategory(includedName);
                if (included == null) return;
                included.resolveSymbols().forEach(symbol -> {
                    // Create symbol with modified id to avoid conflicts
                    var newSymbol = new Symbol(
                            idForCategory(symbol.identifier(), category.name()),
                            symbol.texture(),
                            category.name()
                    );
                    // Register new symbol
                    SymbolRegistry.registerSymbol(newSymbol);
                });
            });
        });
    }

    /**
     * Category JSON format for symbol categories.
     * Important: The category JSON will assign its category to all symbols in the same directory as the JSON file.
     *
     * @param name              name of the category
     * @param includeCategories included list of other categories
     */
    private record CategoryJson(
            String name,
            @Nullable List<String> includeCategories
    ) {
    }
}
