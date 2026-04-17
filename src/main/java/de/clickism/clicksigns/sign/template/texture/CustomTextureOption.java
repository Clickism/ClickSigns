package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record CustomTextureOption(ResourceLocation customTexture) implements TextureOption {
    @Override
    public boolean supports(int width, int height) {
        var texture = new StaticTextureSource(customTexture).resolve(ColorResolver.empty());
        return texture.width() == width && texture.height() == height;
    }

    @Override
    public List<TextureSource> texturesFor(int width, int height) {
        if (!supports(width, height)) {
            return List.of();
        }
        return List.of(new StaticTextureSource(customTexture));
    }
}
