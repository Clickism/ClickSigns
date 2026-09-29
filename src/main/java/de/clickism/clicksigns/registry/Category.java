package de.clickism.clicksigns.registry;

import net.minecraft.resources.Identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a category of symbols.
 */
public class Category<T extends Categorized<T>> {
    private final Identifier identifier;
    private final String name;
    private final Set<Identifier> entries = new HashSet<>();
    private final CategorizedRegistry<T> registry;
    private final int priority;

    /**
     * Creates a new category with the given name.
     *
     * @param identifier the unique identifier for this category
     * @param name       the name of the category
     * @param priority   the priority of the category
     */
    public Category(Identifier identifier, String name, CategorizedRegistry<T> registry, int priority) {
        this.identifier = identifier;
        this.name = name;
        this.registry = registry;
        this.priority = priority;
    }

    /**
     * Adds an entry to this category.
     *
     * @param identifier the unique identifier of the entry to add
     */
    public void add(Identifier identifier) {
        entries.add(identifier);
    }

    /**
     * Gets the unique identifier of this category.
     *
     * @return the unique identifier of this category
     */
    public Identifier identifier() {
        return identifier;
    }

    /**
     * Gets the name of this category.
     *
     * @return the name of this category
     */
    public String displayName() {
        return name;
    }

    /**
     * Gets the priority of this category.
     *
     * @return the priority of this category
     */
    public int priority() {
        return priority;
    }

    /**
     * Gets the set of entry identifiers in this category.
     *
     * @return the set of entry identifiers in this category
     */
    public Set<Identifier> entries() {
        return Collections.unmodifiableSet(entries);
    }

    /**
     * Resolves all entries for this category, including those from included categories.
     *
     * @return a list of all resolved entries, without duplicates
     */
    public List<T> resolveEntries() {
        return entries.stream()
            .map(registry::get)
            .toList();
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category<?> other = (Category<?>) obj;
        return identifier.equals(other.identifier);
    }
}
