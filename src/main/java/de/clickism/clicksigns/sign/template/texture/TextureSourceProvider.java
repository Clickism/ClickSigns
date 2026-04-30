package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.texture.source.TextureSource;

public interface TextureSourceProvider {
    TextureSource getTextureSource(int width, int height);
}
