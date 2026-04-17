package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.registry.TileSetRegistry;
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
        var category = TileSetRegistry.getCategory(this.tileSetCategory);
        if (category == null) {
            return List.of();
        }
        return category.resolveEntries().stream()
                .map(tileSet -> new TiledTextureSource(tileSet.location(), width, height))
                .collect(Collectors.toList());
    }
}
