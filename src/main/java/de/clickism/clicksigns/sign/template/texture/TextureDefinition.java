package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.texture.source.TextureSource;

import java.util.List;

public record TextureDefinition(
        TextureSource defaultTexture,
        List<TextureSource> supportedTextures
) {
}
