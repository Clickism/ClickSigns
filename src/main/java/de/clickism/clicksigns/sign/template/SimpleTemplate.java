package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.template.texture.TextureOption;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SimpleTemplate(
        ResourceLocation identifier,
        String name,
        String description,
        List<TextureOption> frontOptions,
        List<TextureOption> backOptions,
        List<SignElement> elements
) implements Template {
}
