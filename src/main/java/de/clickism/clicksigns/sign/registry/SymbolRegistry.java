package de.clickism.clicksigns.sign.registry;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.texture.Symbol;
import de.clickism.clicksigns.sign.texture.SymbolCategory;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Registry for symbols.
 */
public class SymbolRegistry {
    public static final String UNCATEGORIZED = "uncategorized";
    public static final Symbol ERROR_SYMBOL = new Symbol(
            ClickSigns.identifier("error"),
            new StaticTextureSource(ClickSigns.identifier("error_symbol.png")),
            null
    );

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

    public static Symbol getSymbol(ResourceLocation identifier) {
        return SYMBOLS.getOrDefault(identifier, ERROR_SYMBOL);
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
