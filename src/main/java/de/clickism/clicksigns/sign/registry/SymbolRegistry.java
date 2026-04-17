package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.SymbolCategory;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Registry for symbols.
 */
public class SymbolRegistry {
    public static final String UNCATEGORIZED = "uncategorized";

    private static final Map<ResourceLocation, Symbol> SYMBOLS = new HashMap<>();
    private static final Map<String, SymbolCategory> CATEGORIES = new HashMap<>();

    /**
     * Registers a symbol and adds it to its category.
     *
     * @param symbol the symbol to register
     */
    public static void registerSymbol(Symbol symbol) {
        SYMBOLS.put(symbol.identifier(), symbol);
        CATEGORIES.computeIfAbsent(symbol.category(), SymbolCategory::new).addSymbol(symbol);
    }

    public static @Nullable Symbol getSymbol(ResourceLocation identifier) {
        return SYMBOLS.get(identifier);
    }

    public static @Nullable SymbolCategory getCategory(String name) {
        return CATEGORIES.get(name);
    }

    public static Collection<SymbolCategory> allCategories() {
        return Collections.unmodifiableCollection(CATEGORIES.values());
    }

    public static void clear() {
        SYMBOLS.clear();
        CATEGORIES.clear();
    }
}
