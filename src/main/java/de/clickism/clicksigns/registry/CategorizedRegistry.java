package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Category;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CategorizedRegistry<T extends Categorized<T>> extends Registry<T> {
    protected final Map<ResourceLocation, Category<T>> categories = new HashMap<>();

    public CategorizedRegistry() {
        super();
    }

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

    public void registerCategory(Category<T> category) {
        categories.put(category.identifier(), category);
    }

    public @Nullable Category<T> getCategory(ResourceLocation id) {
        return categories.get(id);
    }

    public boolean hasCategory(ResourceLocation id) {
        return categories.containsKey(id);
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
