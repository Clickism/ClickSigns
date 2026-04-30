package de.clickism.clicksigns.sign.reload;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.layout.FixedLayout;
import de.clickism.clicksigns.sign.template.texture.TextureDefinition;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

public class TemplateListener implements RoadSignReloadListener {

    private static final String TEMPLATE_EXTENSION = ".template.json";

    // TODO: Categories
    @Override
    public void onReload(ResourceManager manager) {
        manager.listResources(
                fromRoot("templates"),
                identifier -> identifier.getPath().endsWith(TEMPLATE_EXTENSION)
        ).forEach((location, resource) -> {
            // Parse json
            var json = fromJsonOrNull(resource, JsonObject.class);
            if (json == null) {
                ClickSigns.LOGGER.error("Failed to parse template json {}.", location.toString());
                return;
            }
            // Get type
            var typeObj = json.get("type");
            if (typeObj == null) {
                ClickSigns.LOGGER.error("Template json {} is missing \"type\" field.", location.toString());
                return;
            }
            var type = typeObj.getAsString();
            // Parse based on type
            switch (type) {
                case "fixed" -> {
                    try {
                        var templateJson = GSON.fromJson(json, FixedTemplateJson.class);
                        var template = templateJson.toTemplate(location);
                        SignRegistries.TEMPLATES.register(template);
                    } catch (Exception e) {
                        ClickSigns.LOGGER.error("Failed to parse fixed template json {}.", location.toString(), e);
                    }
                }
                case "layout" -> {

                }
                default -> {
                    // Unknown template type
                    ClickSigns.LOGGER.error("Unknown template type \"{}\" in template json {}. Ignoring...", type, location.toString());
                }
            }

        });
    }

    private record TemplateMetaJson(
            String name,
            String description,
            String author
    ) {
    }

    private record FixedTemplateJson(
            TemplateMetaJson meta,
            int width,
            int height,
            TextureDefinitionJson front,
            TextureDefinitionJson back,
            List<JsonObject> elements
    ) {
        Template toTemplate(ResourceLocation id) {
            return new Template(
                    id,
                    meta.name,
                    meta.description,
                    null, // TODO: category
                    front.toTextureDefinition(),
                    back.toTextureDefinition(),
                    List.of(), // TODO: text variants
                    new FixedLayout(List.of()) // TODO: elements
            );
        }
    }

    private record TextureDefinitionJson(
            @SerializedName("default")
            ResourceLocation defaultTexture,
            List<ResourceLocation> supported
    ) {
        TextureDefinition toTextureDefinition() {
            // TODO: Supported textures
            if (SignRegistries.TILE_SETS.has(defaultTexture)) {
                return new TextureDefinition(new TiledTextureSource(defaultTexture, 16, 16), List.of());
            }
            return new TextureDefinition(new StaticTextureSource(defaultTexture), List.of());
        }
    }
}
