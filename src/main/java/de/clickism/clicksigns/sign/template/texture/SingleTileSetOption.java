package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SingleTileSetOption(ResourceLocation singleTileSet) implements TextureOption {
    @Override
    public boolean supports(int width, int height) {
        return true;
    }

    @Override
    public List<TextureSource> texturesFor(int width, int height) {
        return List.of(new TiledTextureSource(singleTileSet, width, height));
    }
}
