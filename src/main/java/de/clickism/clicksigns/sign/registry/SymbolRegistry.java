package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.SymbolCategory;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Registry for symbols.
 */
public class SymbolRegistry {
    public static final SymbolCategory UNCATEGORIZED = new SymbolCategory(ClickSigns.identifier("uncategorized"), "Uncategorized");
    public static final Symbol ERROR_SYMBOL = new Symbol(
            ClickSigns.identifier("error"),
            new StaticTextureSource(ClickSigns.identifier("error_symbol.png")),
            null
    );

    private static final Map<ResourceLocation, Symbol> SYMBOLS = new HashMap<>();
    private static final Map<ResourceLocation, SymbolCategory> CATEGORIES = new HashMap<>();

    /**
     * Registers a symbol and adds it to its category.
     *
     * @param symbol the symbol to register
     */
    public static void registerSymbol(Symbol symbol) {
        SYMBOLS.put(symbol.identifier(), symbol);
        if (symbol.categoryId() == null) {
            // Register and add to uncategorized category if the symbol has no category
            CATEGORIES.computeIfAbsent(UNCATEGORIZED.identifier(), id -> UNCATEGORIZED)
                    .addSymbol(symbol);
            return;
        }
        var category = getCategory(symbol.categoryId());
        if (category == null) {
            throw new IllegalStateException("Cannot register symbol " + symbol.identifier() + " with unknown category " + symbol.categoryId());
        }
        category.addSymbol(symbol);
    }

    public static Symbol getSymbol(ResourceLocation identifier) {
        return SYMBOLS.getOrDefault(identifier, ERROR_SYMBOL);
    }

    public static void registerCategory(SymbolCategory category) {
        CATEGORIES.put(category.identifier(), category);
    }

    public static @Nullable SymbolCategory getCategory(ResourceLocation identifier) {
        return CATEGORIES.get(identifier);
    }

    public static Collection<SymbolCategory> allCategories() {
        return Collections.unmodifiableCollection(CATEGORIES.values());
    }

    public static void clear() {
        SYMBOLS.clear();
        CATEGORIES.clear();
    }
}
