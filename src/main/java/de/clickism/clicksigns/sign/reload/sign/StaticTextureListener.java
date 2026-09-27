package de.clickism.clicksigns.sign.reload.sign;

import com.google.gson.annotations.SerializedName;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.StaticTexture;
import de.clickism.clicksigns.sign.reload.DefinedTextureListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

/**
 * Static texture reload listener.
 */
public class StaticTextureListener extends DefinedTextureListener<StaticTextureListener.StaticDefinition, StaticTextureListener.CategoryJson> {
    private static final String STATIC_EXTENSION = ".static.json";
    private static final String STATIC_DIRECTORY = "static";

    /**
     * Creates a new static texture listener.
     */
    public StaticTextureListener() {
        super(SignRegistries.STATIC_TEXTURES, STATIC_DIRECTORY, STATIC_EXTENSION, CategoryJson.class, StaticDefinition.class);
    }

    @Override
    public void onReload(ResourceManager manager) {
        SignRegistries.STATIC_TEXTURE_COLOR_RESOLVERS.clear();
        super.onReload(manager);
    }

    @Override
    protected String categoryName(CategoryJson category) {
        return category.name();
    }

    @Override
    protected int priority(CategoryJson category) {
        return category.priority();
    }

    @Override
    protected void processImage(
        ResourceLocation location,
        Resource resource,
        @Nullable StaticDefinition definition,
        @Nullable ResourceLocation categoryId,
        @Nullable StaticTextureListener.CategoryJson category
    ) {
        if (definition != null) {
            var resolver = definition.colors.toColorResolver();
            SignRegistries.STATIC_TEXTURE_COLOR_RESOLVERS.put(location, resolver);
        }
        SignRegistries.STATIC_TEXTURES.register(new StaticTexture(location, categoryId));
    }

    /**
     * Definition for static textures
     */
    protected record StaticDefinition(
        ColorDefinition colors
    ) {
    }

    /**
     * Category JSON format for tileset category definitions.
     *
     * @param name name of the category
     */
    protected record CategoryJson(
        String name,
        int priority,
        @SerializedName("default")
        @Nullable StaticDefinition defaultDefinition
    ) implements CategoryWithDefault<StaticDefinition> {
    }
}
