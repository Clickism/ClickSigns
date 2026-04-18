package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.sign.Category;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.source.ColorizedTextureSource;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Symbol reload listener.
 */
public class SymbolListener implements RoadSignReloadListener {
    private static final String SYMBOLS_DIR = "symbols";

    @Override
    public void onReload(ResourceManager manager) {
        SymbolRegistry.clear();
        var categories = loadAndRegisterCategories(manager, SYMBOLS_DIR, CategoryJson.class, (identifier, json) -> {
            var category = Category.forSymbol(identifier, json.name());
            SymbolRegistry.registerCategory(category);
        });
        manager.listResources(
                fromRoot(SYMBOLS_DIR),
                identifier -> identifier.getPath().endsWith(".png")
        ).forEach((location, resource) -> {
            var path = location.getPath();
            var directory = stripFileName(path);
            var categoryId = ResourceLocation.tryBuild(location.getNamespace(), directory);
            var category = categories.get(categoryId);
            if (category == null) {
                categoryId = null; // No category
            }
            TextureSource source;
            if (category != null && category.replaceColor != null) {
                var replaceColor = category.replaceColor;
                source = new ColorizedTextureSource(location, replaceColor.from(), replaceColor.to());
            } else {
                source = new StaticTextureSource(location);
            }
            var symbol = new Symbol(location, source, categoryId);
            SymbolRegistry.registerSymbol(symbol);
        });
        resolveIncludedSymbols(categories);
    }

    /**
     * Resolves included symbols for all categories and registers them with modified identifiers to avoid conflicts.
     *
     * @param categories map of categories to resolve included symbols for
     */
    private void resolveIncludedSymbols(Map<ResourceLocation, CategoryJson> categories) {
        // Resolve included categories
        categories.forEach((identifier, category) -> {
            if (category.includeCategories == null) return;
            category.includeCategories.forEach(includedId -> {
                var included = SymbolRegistry.getCategory(includedId);
                if (included == null) return;
                included.resolveEntries().forEach(symbol -> {
                    // Create symbol with modified id to avoid conflicts
                    var newSymbol = new Symbol(
                            symbol.identifierForCategory(symbol.identifier(), identifier),
                            symbol.texture(),
                            identifier
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
            @Nullable List<ResourceLocation> includeCategories,
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
