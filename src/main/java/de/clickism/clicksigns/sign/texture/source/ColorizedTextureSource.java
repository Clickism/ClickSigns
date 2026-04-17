package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.generator.ColorReplacer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Texture source that generates a colorized texture by replacing a specified color in a base texture with another color.
 *
 * @param baseTexture the base texture to colorize
 * @param fromColor   the color to replace (or null to replace all colors)
 * @param toColor     the color to replace with
 */
public record ColorizedTextureSource(
        ResourceLocation baseTexture,
        @Nullable Color fromColor,
        Color toColor
) implements TextureSource {
    /**
     * Creates a colorized text source from integer RGB color values.
     *
     * @param baseTexture the base texture to colorize
     * @param fromColor   the color to replace (or null to replace all colors)
     * @param toColor     the color to replace with
     */
    public ColorizedTextureSource(ResourceLocation baseTexture, @Nullable Integer fromColor, Integer toColor) {
        this(baseTexture, fromColor != null ? new Color(fromColor) : null, new Color(toColor));
    }

    @Override
    public Texture resolve() {
        var texture = new ColorReplacer(baseTexture, fromColor, toColor).getOrGenerate();
        if (texture == null) return ERROR_TEXTURE;
        return texture;
    }
}
