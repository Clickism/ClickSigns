package de.clickism.clicksigns.sign.reload;

import com.google.gson.JsonObject;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.layout.FixedLayout;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clicksigns.util.Size;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Template reload listener.
 */
public class TemplateListener extends CategorizedReloadListener<TemplateListener.CategoryJson> {
    private static final String TEMPLATE_EXTENSION = ".template.json";
    private static final String TEMPLATE_DIRECTORY = "templates";

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
        var type = getTypeOrThrow(json);

        if (type.equals("fixed")) {
            var templateJson = fromJsonOrThrow(json, FixedTemplateJson.class);
            var template = templateJson.toTemplate(location, categoryId);
            SignRegistries.TEMPLATES.register(template);
        } else {
            // Unknown template type
            ClickSigns.LOGGER.error(
                    "Unknown template type \"{}\" in template json {}. Ignoring...",
                    type, location.toString()
            );
        }
    }

    /**
     * Json format for fixed templates.
     *
     * @param meta     metadata for the template
     * @param width    the width of the sign in pixels
     * @param height   the height of the sign in pixels
     * @param front    the texture definition for the front of the sign
     * @param back     the texture definition for the back of the sign
     * @param elements the list of sign elements for the template
     */
    private record FixedTemplateJson(
            Template.Meta meta,
            int width,
            int height,
            ResourceLocation front,
            ResourceLocation back,
            List<JsonObject> elements
    ) {
        private static final SignElementParser ELEMENT_PARSER = new SignElementParser();

        /**
         * Converts the json into a template object
         */
        private Template toTemplate(ResourceLocation id, ResourceLocation categoryId) {
            var parsedElements = elements.stream()
                    .map(ELEMENT_PARSER::parse)
                    .toList();
            return new Template(
                    id,
                    meta,
                    categoryId,
                    parseTexture(front),
                    parseTexture(back),
                    List.of(), // TODO: text variants
                    new FixedLayout(parsedElements, new Size(width, height))
            );
        }

        /**
         * Parses the given texture identifier as tileset or static texture.
         *
         * @param location the texture identifier to parse
         * @return texture source
         */
        private TextureSource parseTexture(ResourceLocation location) {
            if (SignRegistries.TILE_SETS.has(location)) {
                return new TiledTextureSource(location, width, height);
            }
            return new StaticTextureSource(location);
        }
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
