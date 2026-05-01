package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Category;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry for categorized entries. Extends the basic {@link Registry} with support for categories.
 * Entries that implement the {@link Categorized} interface can belong to a category, which is represented by the {@link Category} class.
 * Categories are registered separately from entries, and can be retrieved by their identifier.
 *
 * @param <T> the type of the registered entries, which must implement the {@link Categorized} interface
 */
public class CategorizedRegistry<T extends Categorized<T>> extends Registry<T> {
    /**
     * Map from category id to category
     */
    protected final Map<ResourceLocation, Category<T>> categories = new HashMap<>();

    /**
     * Creates a new categorized registry with no default entry.
     */
    public CategorizedRegistry() {
        super();
    }

    /**
     * Creates a new categorized registry with the given default entry.
     *
     * @param defaultEntry the default entry to return when an identifier is not found
     */
    public CategorizedRegistry(T defaultEntry) {
        super(defaultEntry);
    }

    @Override
    public void register(T entry) {
        super.register(entry);
        // Add to category if it has one when registering
        var categoryId = entry.categoryId();
        if (categoryId == null) return;
        var category = categories.get(categoryId);
        if (category == null) return;
        category.add(entry.identifier());
    }

    /**
     * Registers the given category.
     * Will override any existing category with the same identifier.
     *
     * @param category the category to register
     */
    public void registerCategory(Category<T> category) {
        categories.put(category.identifier(), category);
    }

    /**
     * Gets the category with the given identifier, or null if it doesn't exist.
     *
     * @param id the identifier of the category to get
     * @return the category with the given identifier, or null if it doesn't exist
     */
    public @Nullable Category<T> getCategory(ResourceLocation id) {
        return categories.get(id);
    }

    /**
     * Checks if a category with the given identifier exists in the registry.
     *
     * @param id the identifier to check for
     * @return true if a category with the given identifier exists, false otherwise
     */
    public boolean hasCategory(ResourceLocation id) {
        return categories.containsKey(id);
    }

    /**
     * Gets all registered categories in the registry.
     *
     * @return unmodifiable collection of all registered categories
     */
    public Collection<Category<T>> allCategories() {
        return Collections.unmodifiableCollection(categories.values());
    }

    /**
     * Creates a new category with the given identifier and name, associated with this registry.
     *
     * @param identifier the identifier of the category
     * @param name       the display name of the category
     * @return the created category
     */
    public Category<T> createCategory(ResourceLocation identifier, String name) {
        return new Category<>(identifier, name, this);
    }

    /**
     * Creates a new category with the given identifier and name, associated with this registry, and registers it.
     *
     * @param identifier the identifier of the category
     * @param name       the display name of the category
     * @return the created and registered category
     */
    public Category<T> createAndRegisterCategory(ResourceLocation identifier, String name) {
        var category = createCategory(identifier, name);
        registerCategory(category);
        return category;
    }

    @Override
    public void clear() {
        super.clear();
        categories.clear();
    }
}
