package de.clickism.clicksigns.sign.template;

import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface Template {
    ResourceLocation identifier();

    String name();

    String description();

    TextureSource frontTextures();

    TextureSource backTextures();

    List<RoadSignElement> elements();

    /**
     * Template type
     */
    enum Type {
        FIXED,
    }

}
