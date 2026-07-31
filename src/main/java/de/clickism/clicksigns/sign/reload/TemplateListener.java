package de.clickism.clicksigns.sign.reload;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.TemplateParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

/**
 * Template reload listener.
 */
public class TemplateListener extends CategorizedReloadListener<TemplateListener.CategoryJson> {
    private static final String TEMPLATE_EXTENSION = ".template.json";
    private static final String TEMPLATE_DIRECTORY = "templates";

    private static final TemplateParser TEMPLATE_PARSER = new TemplateParser();

    /**
     * Creates a new template listener.
     */
    public TemplateListener() {
        super(SignRegistries.TEMPLATES, TEMPLATE_DIRECTORY, TEMPLATE_EXTENSION, CategoryJson.class);
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
        var json = fromJsonOrThrow(resource, JsonObject.class);
        var template = TEMPLATE_PARSER.parse(json, location, categoryId);
        SignRegistries.TEMPLATES.register(template);
    }

    /**
     * Category JSON format for template categories.
     *
     * @param name name of the category
     */
    protected record CategoryJson(
            String name
    ) {
    }
}
