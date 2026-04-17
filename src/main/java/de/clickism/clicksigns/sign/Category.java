package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.sign.texture.TileSet;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Represents a category of symbols.
 */
public class Category<T> {
    private final ResourceLocation identifier;
    private final String name;
    private final Set<ResourceLocation> entries = new HashSet<>();
    private final Function<ResourceLocation, T> resolver;

    /**
     * Creates a new category with the given name.
     *
     * @param identifier the unique identifier for this category
     * @param name       the name of the category
     */
    public Category(ResourceLocation identifier, String name, Function<ResourceLocation, T> resolver) {
        this.identifier = identifier;
        this.name = name;
        this.resolver = resolver;
    }

    public static Category<Symbol> forSymbol(ResourceLocation identifier, String name) {
        return new Category<>(identifier, name, SymbolRegistry::getSymbol);
    }

    public static Category<TileSet> forTileSet(ResourceLocation identifier, String name) {
        return new Category<>(identifier, name, TileSetRegistry::getTileSet);
    }

    /**
     * Adds an entry to this category.
     *
     * @param identifier the unique identifier of the entry to add
     */
    public void add(ResourceLocation identifier) {
        entries.add(identifier);
    }

    /**
     * Gets the unique identifier of this category.
     *
     * @return the unique identifier of this category
     */
    public ResourceLocation identifier() {
        return identifier;
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
     * Gets the set of entry identifiers in this category.
     *
     * @return the set of entry identifiers in this category
     */
    public Set<ResourceLocation> entries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * Resolves all entries for this category, including those from included categories.
     *
     * @return a list of all resolved entries, without duplicates
     */
    public List<T> resolveEntries() {
        return entries.stream()
                .map(resolver)
                .toList();
    }
}
