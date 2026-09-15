package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a static texture that can be used for signs.
 *
 * @param identifier the resource location of the texture
 * @param categoryId optional category id for this texture, used for grouping textures in the sign editor
 */
public record StaticTexture(
    ResourceLocation identifier,
    @Nullable ResourceLocation categoryId
) implements Categorized<StaticTexture> {
    @Override
    public CategorizedRegistry<StaticTexture> registry() {
        return SignRegistries.STATIC_TEXTURES;
    }

    /**
     * Returns a texture source for this static texture.
     *
     * @return a texture source that provides the texture for this static texture
     */
    public TextureSource textureSource() {
        return TextureSource.ofStatic(identifier);
    }
}
