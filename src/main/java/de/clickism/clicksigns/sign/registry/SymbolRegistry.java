package de.clickism.clicksigns.sign.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for symbols.
 */
public class SymbolRegistry {
    /**
     * Category for symbols that don't have a category.
     */
    public static final String UNCATEGORIZED = "uncategorized";

    private static final Map<String, List<ResourceLocation>> BY_CATEGORY = new HashMap<>();
    private static final Map<ResourceLocation, String> BY_SYMBOL = new HashMap<>();

    /**
     * Registers a symbol in the given category.
     *
     * @param category category to register the symbol in
     * @param symbol   resource location of the symbol to register
     */
    public static void register(String category, ResourceLocation symbol) {
        BY_CATEGORY.computeIfAbsent(category, k -> new ArrayList<>()).add(symbol);
        BY_SYMBOL.put(symbol, category);
    }

    /**
     * Gets the category of the given symbol.
     *
     * @param symbol resource location of the symbol to get the category of
     * @return the category of the given symbol, or "uncategorized" if the symbol is not registered or has no category
     */
    public static String categoryOf(ResourceLocation symbol) {
        return BY_SYMBOL.getOrDefault(symbol, UNCATEGORIZED);
    }

    /**
     * Gets a list of all registered symbols in the given category.
     *
     * @param category category to get symbols for
     * @return a list of all registered symbols in the given category, or an empty list
     */
    public static List<ResourceLocation> allInCategory(String category) {
        return BY_CATEGORY.getOrDefault(category, List.of());
    }

    /**
     * Clears all registered symbols.
     */
    public static void clear() {
        BY_CATEGORY.clear();
        BY_SYMBOL.clear();
    }
}
