package de.clickism.clicksigns.sign.reload;

import com.google.gson.annotations.SerializedName;
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
            var base = fromJsonOrNull(resource, BaseTemplateJson.class);
            if (base == null) return;
            switch (base.type()) {
                case "fixed" -> {
                    var fixed = fromJsonOrNull(resource, FixedTemplateJson.class);
                    if (fixed == null) return;
                    var templateId = stripExtension(location, TEMPLATE_EXTENSION);
                    var template = new Template(
                            templateId,
                            base.name,
                            base.description,
                            null, // TODO: category
                            fixed.front.toTextureDefinition(),
                            fixed.back.toTextureDefinition(),
                            List.of(), // TODO: text variants
                            new FixedLayout(List.of()) // TODO: elements
                    );
                }
                default -> {
                    // Unknown template type
                }
            }

        });
    }

    private record BaseTemplateJson(
            String type,
            String name,
            String description
    ) {
    }

    private record FixedTemplateJson(
            int width,
            int height,
            TextureDefinitionJson front,
            TextureDefinitionJson back,
            List<BaseElementJson> elements
    ) {
    }

    private record BaseElementJson(
            String type,
            int x,
            int y
    ) {
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
