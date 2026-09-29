package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.CategorizedRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleReloadListener<C> extends CategorizedListener<C> {
    public SimpleReloadListener(
        CategorizedRegistry<?> registry,
        String subDirectory,
        String fileSuffix,
        Class<C> categoryClass
    ) {
        super(registry, subDirectory, categoryClass);
        registerProcessor(fileSuffix, this::processResource);
    }

    protected abstract void processResource(
        Identifier location,
        Resource resource,
        @Nullable Identifier categoryId,
        @Nullable C category
    );
}
