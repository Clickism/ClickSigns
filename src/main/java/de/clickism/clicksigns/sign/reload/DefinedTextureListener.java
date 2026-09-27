package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.CategorizedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class DefinedTextureListener<T, C extends DefinedTextureListener.CategoryWithDefault<T>>
    extends CategorizedListener<C> {

    private final Map<ResourceLocation, T> definitions = new HashMap<>();

    private final Class<T> definitionClass;

    public DefinedTextureListener(
        CategorizedRegistry<?> registry,
        String subDirectory,
        String definitionSuffix,
        Class<C> categoryClass,
        Class<T> definitionClass
    ) {
        super(registry, subDirectory, categoryClass);
        this.definitionClass = definitionClass;
        registerProcessor(definitionSuffix, this::processDefinition);
        registerProcessor(".png", ((location, resource, categoryId, category) -> {
            var definitionLocation = replaceExtension(location, ".png", definitionSuffix);
            var definition = definitionOf(definitionLocation, category);
            processImage(location, resource, definition, categoryId, category);
        }));
    }

    @Override
    public void onReload(ResourceManager manager) {
        definitions.clear();
        super.onReload(manager);
    }

    protected abstract void processImage(
        ResourceLocation location,
        Resource resource,
        @Nullable T definition,
        @Nullable ResourceLocation categoryId,
        @Nullable C category
    );

    protected void processDefinition(
        ResourceLocation location,
        Resource resource,
        @Nullable ResourceLocation categoryId,
        @Nullable C category
    ) {
        var definition = fromJsonOrThrow(resource, definitionClass);
        definitions.put(location, definition);
    }

    protected @Nullable T definitionOf(ResourceLocation location, @Nullable C category) {
        var defined = definitions.get(location);
        if (defined != null) {
            return defined;
        }
        if (category != null) {
            return category.defaultDefinition();
        }
        return null;
    }

    protected interface CategoryWithDefault<T> {
        T defaultDefinition();
    }
}
