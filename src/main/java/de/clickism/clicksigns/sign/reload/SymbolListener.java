package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.texture.Symbol;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.source.ColorizedTextureSource;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

/**
 * Symbol reload listener.
 */
public class SymbolListener implements RoadSignReloadListener {
    @Override
    public void onReload(ResourceManager manager) {
        SymbolRegistry.clear();
        var directoryToCategory = loadCategories(manager);
        manager.listResources(
                fromRoot("symbols"),
                identifier -> identifier.getPath().endsWith(".png")
        ).forEach((location, resource) -> {
            var path = location.getPath();
            var directory = path.substring(0, path.lastIndexOf('/'));
            var category = directoryToCategory.get(directory);
            var categoryName = category != null ? category.name() : SymbolRegistry.UNCATEGORIZED;
            TextureSource source;
            if (category != null && category.replaceColor != null) {
                var replaceColor = category.replaceColor;
                source = new ColorizedTextureSource(location, replaceColor.from(), replaceColor.to());
            } else {
                source = new StaticTextureSource(location);
            }
            var symbol = new Symbol(location, source, categoryName);
            SymbolRegistry.registerSymbol(symbol);
        });
        resolveIncludedSymbols(directoryToCategory.values());
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
                fromRoot("symbols"),
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
                            symbol.identifierForCategory(symbol.identifier(), category.name()),
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
            @Nullable List<String> includeCategories,
            @Nullable SymbolListener.ReplaceColorJson replaceColor
    ) {
    }

    /**
     * Color replacement JSON format for symbol categories.
     *
     * @param from color to replace.
     * @param to   color to replace with.
     */
    private record ReplaceColorJson(
            @Nullable String from,
            String to
    ) {
    }
}
