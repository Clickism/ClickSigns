package de.clickism.clicksigns.sign.reload;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.serialization.JsonTagImpl;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Symbol reload listener.
 */
public class SymbolListener extends DefinedTextureListener<SymbolListener.SymbolDefinition, SymbolListener.CategoryJson> {
    private static final String SYMBOL_DIRECTORY = "symbols";
    private static final String SYMBOL_EXTENSION = ".symbol.json";

    /**
     * Creates a new symbol listener.
     */
    public SymbolListener() {
        super(SignRegistries.SYMBOLS, SYMBOL_DIRECTORY, SYMBOL_EXTENSION, CategoryJson.class, SymbolDefinition.class);
    }

    @Override
    protected String categoryName(CategoryJson category) {
        return category.name();
    }

    @Override
    protected void processImage(
        ResourceLocation location,
        Resource resource,
        @Nullable SymbolListener.SymbolDefinition definition,
        @Nullable ResourceLocation categoryId,
        @Nullable SymbolListener.CategoryJson category
    ) {
        TextureSource textureSource = TextureSource.ofStatic(location);
        if (definition != null && definition.texture != null) {
            var textureJson = definition.texture;
            textureJson.addProperty("base", location.toString());
            try {
                textureSource = TextureSource.codec().tagReader().read(new JsonTagImpl(textureJson));
            } catch (Exception e) {
                ClickSigns.LOGGER.error("Failed to read texture processors for symbol: {}: {}", location, e.getMessage());
            }
        }
        var symbol = new Symbol(location, textureSource, categoryId);
        SignRegistries.SYMBOLS.register(symbol);
    }

    @Override
    protected void processCategory(ResourceLocation categoryId, CategoryJson category) {
        // Resolves included symbols for all categories and registers them with modified identifiers to avoid conflicts.
        if (category.includeCategories == null) return;
        category.includeCategories.forEach(includedId -> {
            var included = SignRegistries.SYMBOLS.getCategoryOrNull(includedId);
            if (included == null) return;
            included.resolveEntries().forEach(symbol -> {
                // Create symbol with modified id to avoid conflicts
                var newSymbol = new Symbol(
                    symbol.identifierForCategory(symbol.identifier(), categoryId),
                    symbol.textureSource(),
                    categoryId
                );
                // Register new symbol
                SignRegistries.SYMBOLS.register(newSymbol);
            });
        });
    }

    /**
     * Symbol JSON format for symbol definitions.
     *
     * @param texture supposed to be a {@link TextureSource} object, without the "base" field,
     *                as that is automatically set to the symbol's identifier.
     */
    protected record SymbolDefinition(
        @Nullable JsonObject texture
    ) {
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
        @SerializedName("default")
        @Nullable SymbolDefinition defaultDefinition
    ) implements CategoryWithDefault<SymbolDefinition> {
    }
}
