package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.StaticTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

/**
 * Static texture reload listener.
 */
public class StaticTextureListener extends CategorizedReloadListener<StaticTextureListener.CategoryJson> {
    private static final String STATIC_DIRECTORY = "static";

    /**
     * Creates a new static texture listener.
     */
    public StaticTextureListener() {
        super(SignRegistries.STATIC_TEXTURES, STATIC_DIRECTORY, ".png", CategoryJson.class);
    }

    @Override
    protected String categoryName(CategoryJson category) {
        return category.name();
    }

    @Override
    protected void processResource(
        ResourceLocation location,
        Resource resource,
        @Nullable ResourceLocation categoryId,
        @Nullable CategoryJson category
    ) {
        SignRegistries.STATIC_TEXTURES.register(new StaticTexture(location, categoryId));
    }

    /**
     * Category JSON format for tileset category definitions.
     *
     * @param name name of the category
     */
    protected record CategoryJson(
        String name
    ) {
    }
}
