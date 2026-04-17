package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Represents a category of symbols.
 */
public class SymbolCategory {
    private final String name;
    private final Set<ResourceLocation> symbols = new HashSet<>();

    /**
     * Creates a new symbol category with the given name.
     *
     * @param name the name of the category
     */
    public SymbolCategory(String name) {
        this.name = name;
    }

    /**
     * Adds a symbol to this category.
     *
     * @param symbol the symbol to add
     */
    public void addSymbol(Symbol symbol) {
        symbols.add(symbol.identifier());
    }

    /**
     * Gets the name of this category.
     *
     * @return the name of this category
     */
    public String name() {
        return name;
    }

    /**
     * Gets the set of symbol identifiers in this category.
     *
     * @return the set of symbol identifiers in this category
     */
    public Set<ResourceLocation> symbols() {
        return Collections.unmodifiableSet(symbols);
    }

    /**
     * Resolves all symbols for this category, including those from included categories.
     *
     * @return a list of all resolved symbols, without duplicates
     */
    public List<Symbol> resolveSymbols() {
        return symbols.stream()
                .map(SymbolRegistry::getSymbol)
                .filter(Objects::nonNull)
                .toList();
    }
}
