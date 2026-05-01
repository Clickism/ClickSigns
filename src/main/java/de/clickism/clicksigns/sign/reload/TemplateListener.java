package de.clickism.clicksigns.sign.reload;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.layout.FixedLayout;
import de.clickism.clicksigns.sign.template.texture.TextureDefinition;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        switch (type) {
            case "fixed" -> {
                var templateJson = fromJsonOrThrow(json, FixedTemplateJson.class);
                var template = templateJson.toTemplate(location, categoryId);
                SignRegistries.TEMPLATES.register(template);
            }
            case "layout" -> {
                // TODO: Implement layout templates
            }
            default -> {
                // Unknown template type
                ClickSigns.LOGGER.error("Unknown template type \"{}\" in template json {}. Ignoring...", type, location.toString());
            }
        }
    }

    private record FixedTemplateJson(
            Template.Meta meta,
            int width,
            int height,
            TextureDefinitionJson front,
            TextureDefinitionJson back,
            List<JsonObject> elements
    ) {
        private static final SignElementParser ELEMENT_PARSER = new SignElementParser();

        Template toTemplate(ResourceLocation id, ResourceLocation categoryId) {
            var parsedElements = elements.stream()
                    .map(ELEMENT_PARSER::parse)
                    .toList();
            return new Template(
                    id,
                    meta,
                    categoryId,
                    front.toTextureDefinition(),
                    back.toTextureDefinition(),
                    List.of(), // TODO: text variants
                    new FixedLayout(parsedElements)
            );
        }
    }

    /**
     * Texture definition json format for template texture definitions.
     * <p>
     * Supported textures can hold one of the following:
     * - A single custom texture (e.g. "clicksigns:custom/my_texture.png")
     * - A single tile set (e.g. "clicksigns:tilesets/cool/blue.png")
     * - A category of tile sets, with a # before it (e.g. "#clicksigns:tilesets/cool")
     *
     * @param defaultTexture the default texture to use for the template
     * @param supported      set of supported textures for the template
     */
    private record TextureDefinitionJson(
            @SerializedName("default")
            ResourceLocation defaultTexture,
            List<String> supported
    ) {
        TextureDefinition toTextureDefinition() {
            var parsedSupported = parseSupportedTextures();
            if (SignRegistries.TILE_SETS.has(defaultTexture)) {
                return new TextureDefinition(TiledTextureSource.unsized(defaultTexture), parsedSupported);
            }
            return new TextureDefinition(new StaticTextureSource(defaultTexture), parsedSupported);
        }

        /**
         * Parses the supported textures for the template.
         *
         * @return a list of supported texture sources
         */
        List<TextureSource> parseSupportedTextures() {
            return supported.stream()
                    .flatMap(idOrCategory -> {
                        // Check if category
                        var categoryId = getCategoryId(idOrCategory);
                        var category = SignRegistries.TILE_SETS.getCategory(categoryId);
                        if (category != null) {
                            // Add all tilesets in category
                            return category.resolveEntries().stream()
                                    .map(tileSet -> TiledTextureSource.unsized(tileSet.identifier()));
                        }
                        // Not a category, check if tile set
                        var location = ResourceLocation.tryParse(idOrCategory);
                        if (SignRegistries.TILE_SETS.has(location)) {
                            return Stream.of(TiledTextureSource.unsized(location));
                        }
                        // Not a tile set, treat as static texture
                        return Stream.of(new StaticTextureSource(location));
                    })
                    .collect(Collectors.toList());
        }

        @Nullable ResourceLocation getCategoryId(String id) {
            if (id.startsWith("#")) {
                return ResourceLocation.tryParse(id.substring(1));
            }
            return null;
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
