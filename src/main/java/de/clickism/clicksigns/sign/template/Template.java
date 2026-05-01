package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.template.layout.FixedLayout;
import de.clickism.clicksigns.sign.template.layout.Layout;
import de.clickism.clicksigns.sign.template.texture.TextureDefinition;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
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
        TextureDefinition front,
        TextureDefinition back,
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

    public static final Template TEST = new Template(
            ClickSigns.identifier("test"),
            new Template.Meta(
                    "Test Sign",
                    "A test sign template for demonstration purposes.",
                    "Clickimsm"
            ),
            null,
            new TextureDefinition(TiledTextureSource.unsized(ClickSigns.signAsset("tilesets/default/white.png")), List.of()),
            new TextureDefinition(TiledTextureSource.unsized(ClickSigns.signAsset("tilesets/backs/back.png")), List.of()),
            List.of(new TextVariant("Default", "#FFFFFF", null)),
            new FixedLayout(RoadSign.DEFAULT.elements())
    );

    /**
     * Builds a road sign based on this template with the given dimensions.
     *
     * @param width  the width of the sign in pixels
     * @param height the height of the sign in pixels
     * @return a new RoadSign instance based on this template and the specified dimensions
     */
    RoadSign build(int width, int height) {
        var elements = layout.build(new Size(width, height));
        return new RoadSign(
                front.defaultTexture().resize(width, height),
                back.defaultTexture().resize(width, height),
                elements,
                RoadSign.DEFAULT_ALIGNMENT,
                this.identifier
        );
    }

    @Override
    public CategorizedRegistry<Template> registry() {
        return SignRegistries.TEMPLATES;
    }
}
