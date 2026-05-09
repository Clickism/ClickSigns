package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Category;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * An interface for objects that belong to a category.
 *
 * @param <T> the type of the categorized object
 */
public interface Categorized<T extends Identifiable & Categorized<T>> extends Identifiable {
    /**
     * Gets the identifier of the category this object belongs to, or null if it doesn't belong to any category.
     *
     * @return category identifier, or null
     */
    @Nullable ResourceLocation categoryId();

    /**
     * Gets the registry this object belongs to.
     *
     * @return the registry this object belongs to
     */
    CategorizedRegistry<T> registry();

    /**
     * Resolves the category of this object from the registry.
     *
     * @return the resolved category, or null if it doesn't belong to any category or the category doesn't exist
     */
    default @Nullable Category<T> resolveCategory() {
        return registry().getCategory(categoryId());
    }

    /**
     * Gets the next entry in the same category.
     * If no category is assigned or the category doesn't exist, returns this object.
     *
     * @return the next entry in the same category
     */
    @SuppressWarnings("unchecked")
    default T nextInCategory() {
        var category = resolveCategory();
        if (category == null) return (T) this;
        var items = new ArrayList<>(category.entries());
        int index = items.indexOf(this.identifier());
        if (index == -1) return (T) this;
        var nextId = items.get((index + 1) % items.size());
        return registry().getOrThrow(nextId);
    }
}
