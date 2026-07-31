package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clicksigns.util.PixelSized;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Template(
        Meta meta,
        Sign sign,
        // Other data
        // TODO: Maybe remove identifier at all? or make it nullable. Not needed for local templates
        ResourceLocation identifier,
        @Nullable ResourceLocation categoryId
) implements Categorized<Template> {
    /**
     * Builds a road sign based on this template.
     *
     * @return a new RoadSign instance based on this template and the specified dimensions
     */
    public RoadSign build() {
        return sign.build(identifier);
    }

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
        /**
         * Creates a placeholder meta instance with default values.
         *
         * @return a new Meta instance with placeholder values
         */
        public static Meta placeholder() {
            var name = ComponentUtil.render(Component.translatable("clicksigns.template.placeholder.name"));
            var description = ComponentUtil.render(Component.translatable("clicksigns.template.placeholder.description"));
            var author = ComponentUtil.render(Component.translatable("clicksigns.template.placeholder.author"));
            return new Meta(name, description, author);
        }
    }

    /**
     * Sign data for a sign template.
     *
     * @param width    the width of the sign in pixels
     * @param height   the height of the sign in pixels
     * @param front    the front texture source of the sign
     * @param back     the back texture source of the sign
     * @param elements the list of sign elements for the sign
     */
    public record Sign(
            int width,
            int height,
            TextureSource front,
            TextureSource back,
            List<SignElement> elements
    ) implements PixelSized {
        /**
         * Builds a road sign based on this sign data.
         *
         * @param identifier the resource location identifier for the road sign
         * @return a new RoadSign instance based on this sign data and the specified dimensions
         */
        private RoadSign build(ResourceLocation identifier) {
            return new RoadSign(
                    front.resize(width, height),
                    back.resize(width, height),
                    elements,
                    RoadSign.DEFAULT_ALIGNMENT,
                    identifier
            );
        }
    }

    @Override
    public CategorizedRegistry<Template> registry() {
        return SignRegistries.RESOURCE_TEMPLATES;
    }
}
