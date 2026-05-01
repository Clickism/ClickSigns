package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record TextureDefinition(
        TextureSource defaultTexture,
        List<TextureSource> supportedTextures
        // TODO: Maybe refactor into separate fields for tile sets and custom textures
) {
}
