package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.CategorizedRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class DefinedTextureListener<T, C extends DefinedTextureListener.CategoryWithDefault<T>>
    extends CategorizedListener<C> {

    private final Map<Identifier, T> definitions = new HashMap<>();

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
        Identifier location,
        Resource resource,
        @Nullable T definition,
        @Nullable Identifier categoryId,
        @Nullable C category
    );

    protected void processDefinition(
        Identifier location,
        Resource resource,
        @Nullable Identifier categoryId,
        @Nullable C category
    ) {
        var definition = fromJsonOrThrow(resource, definitionClass);
        definitions.put(location, definition);
    }

    protected @Nullable T definitionOf(Identifier location, @Nullable C category) {
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
