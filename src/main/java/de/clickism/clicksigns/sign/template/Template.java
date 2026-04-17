package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.template.texture.TextureOption;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface Template {
    ResourceLocation identifier();

    String name();

    String description();

    List<TextureOption> frontOptions();

    List<TextureOption> backOptions();

    List<SignElement> elements();

    /**
     * Template type
     */
    enum Type {
        FIXED,
    }

}
