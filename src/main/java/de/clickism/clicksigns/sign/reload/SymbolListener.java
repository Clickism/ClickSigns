package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.texture.source.ColorizedTextureSource;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Symbol reload listener.
 */
public class SymbolListener extends CategorizedReloadListener<SymbolListener.CategoryJson> {
    private static final String SYMBOL_DIRECTORY = "symbols";
    private static final String SYMBOL_EXTENSION = ".png";

    /**
     * Creates a new symbol listener.
     */
    public SymbolListener() {
        super(SignRegistries.SYMBOLS, SYMBOL_DIRECTORY, SYMBOL_EXTENSION, CategoryJson.class);
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
        TextureSource source;
        if (category != null && category.replaceColor != null) {
            var replaceColor = category.replaceColor;
            source = new ColorizedTextureSource(location, replaceColor.from(), replaceColor.to());
        } else {
            source = new StaticTextureSource(location);
        }
        var symbol = new Symbol(location, source, categoryId);
        SignRegistries.SYMBOLS.register(symbol);
    }

    @Override
    protected void processCategory(ResourceLocation categoryId, CategoryJson category) {
        // Resolves included symbols for all categories and registers them with modified identifiers to avoid conflicts.
        if (category.includeCategories == null) return;
        category.includeCategories.forEach(includedId -> {
            var included = SignRegistries.SYMBOLS.getCategory(includedId);
            if (included == null) return;
            included.resolveEntries().forEach(symbol -> {
                // Create symbol with modified id to avoid conflicts
                var newSymbol = new Symbol(
                        symbol.identifierForCategory(symbol.identifier(), categoryId),
                        symbol.texture(),
                        categoryId
                );
                // Register new symbol
                SignRegistries.SYMBOLS.register(newSymbol);
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
    protected record CategoryJson(
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
    protected record ReplaceColorJson(
            @Nullable String from,
            String to
    ) {
    }
}
