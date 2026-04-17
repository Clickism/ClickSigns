package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.generator.ColorReplacer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Texture source that generates a colorized texture by replacing a specified color in a base texture with another color.
 *
 * @param baseTexture the base texture to colorize
 * @param fromColor   the color to replace (or null to replace all colors)
 * @param toColor     the color to replace with
 */
public record ColorizedTextureSource(
        ResourceLocation baseTexture,
        @Nullable String fromColor,
        String toColor
) implements TextureSource {
    /**
     * Type key
     */
    public static final String TYPE = "colorized";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public Texture resolve(ColorResolver colorResolver) {
        var from = fromColor != null ? colorResolver.resolve(fromColor) : null;
        var to = colorResolver.resolve(toColor);
        var texture = new ColorReplacer(baseTexture, from, to).getOrGenerate();
        if (texture == null) return ERROR_TEXTURE;
        return texture;
    }
}
