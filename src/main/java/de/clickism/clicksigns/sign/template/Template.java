package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public record Template(
    Meta meta,
    RoadSign signData,
    // Other data
    @NotNull ResourceLocation identifier,
    @Nullable ResourceLocation categoryId
) implements Categorized<Template> {
    /**
     * Builds a road sign based on this template.
     *
     * @return a new RoadSign instance based on this template and the specified dimensions
     */
    public RoadSign roadSign() {
        return signData.withAlignment(RoadSign.DEFAULT_ALIGNMENT);
    }

    @Override
    public CategorizedRegistry<Template> registry() {
        return SignRegistries.RESOURCE_TEMPLATES;
    }

    /**
     * Metadata for a sign template.
     *
     * @param name   the display name of the template
     * @param author the author of the template
     */
    public record Meta(
        String name,
        @Nullable String author
    ) {
        /**
         * Creates a placeholder meta instance with default values.
         *
         * @return a new Meta instance with placeholder values
         */
        public static Meta placeholder() {
            var name = t("clicksigns.template.placeholder.name").getString();
            var author = t("clicksigns.template.placeholder.author").getString();
            return new Meta(name, author);
        }
    }
}
