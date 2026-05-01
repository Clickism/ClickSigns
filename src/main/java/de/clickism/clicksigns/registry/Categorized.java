package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Category;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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
     * @return the resolved category, or null if it doesn't belong to any category or the category doesn't exist
     */
    default @Nullable Category<T> resolveCategory() {
        return registry().getCategory(categoryId());
    }
}
