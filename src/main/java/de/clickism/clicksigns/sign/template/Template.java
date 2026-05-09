package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.template.layout.Layout;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.Size;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Template(
        // Metadata
        ResourceLocation identifier,
        Meta meta,
        @Nullable ResourceLocation categoryId,
        // Sign data
        TextureSource front,
        TextureSource back,
        List<TextVariant> textVariants,
        Layout layout
) implements Categorized<Template> {
    /**
     * Metadata for a sign template.
     *
     * @param name        the display name of the template
     * @param description a brief description of the template
     * @param author      the author of the template
     */
    public record Meta(
            String name,
            String description,
            @Nullable String author
    ) {
    }

    /**
     * Builds a road sign based on this template with the given dimensions.
     *
     * @param width  the width of the sign in pixels
     * @param height the height of the sign in pixels
     * @return a new RoadSign instance based on this template and the specified dimensions
     */
    public RoadSign build(int width, int height) {
        var elements = layout.build(new Size(width, height));
        return new RoadSign(
                front.resize(width, height),
                back.resize(width, height),
                elements,
                RoadSign.DEFAULT_ALIGNMENT,
                this.identifier
        );
    }

    public RoadSign buildDefault() {
        return build(layout.defaultSize().width(), layout.defaultSize().height());
    }

    @Override
    public CategorizedRegistry<Template> registry() {
        return SignRegistries.TEMPLATES;
    }
}
