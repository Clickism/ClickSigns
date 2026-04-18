package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Category;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CategorizedRegistry<T extends Identifiable> extends Registry<T> {
    protected final Map<ResourceLocation, Category<T>> categories = new HashMap<>();

    public void registerCategory(Category<T> category) {
        categories.put(category.identifier(), category);
    }

    public @Nullable Category<T> getCategory(ResourceLocation id) {
        return categories.get(id);
    }

    public Collection<Category<T>> allCategories() {
        return Collections.unmodifiableCollection(categories.values());
    }

    public Category<T> createCategory(ResourceLocation identifier, String name) {
        return new Category<>(identifier, name, this);
    }

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
