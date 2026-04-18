package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.stream.Collectors;

public record TileSetCategoryOption(ResourceLocation tileSetCategory) implements TextureOption {
    @Override
    public boolean supports(int width, int height) {
        return true;
    }

    @Override
    public List<TextureSource> texturesFor(int width, int height) {
        var category = SignRegistries.TILE_SETS.getCategory(this.tileSetCategory);
        if (category == null) {
            return List.of();
        }
        return category.resolveEntries().stream()
                .map(tileSet -> new TiledTextureSource(tileSet.identifier(), width, height))
                .collect(Collectors.toList());
    }
}
