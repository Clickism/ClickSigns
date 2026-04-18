package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Category;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface Categorized<T extends Identifiable> {
    @Nullable ResourceLocation categoryId();

    CategorizedRegistry<T> registry();

    default Category<T> resolveCategory() {
        return registry().getCategory(categoryId());
    }
}
