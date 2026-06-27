package de.clickism.clicksigns.sign.texture.generator;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Texture generator that replaces colors in a base texture with another color, while preserving alpha values.
 */
public class ColorReplacer extends CachedTextureGenerator {
    private final ResourceLocation texture;
    private final @Nullable Integer fromColor;
    private final Integer toColor;

    /**
     * Replaces all RGB values in the base texture with the given color.
     *
     * @param texture base texture
     * @param toColor color to replace with
     */
    public ColorReplacer(ResourceLocation texture, Color toColor) {
        this(texture, null, toColor);
    }

    /**
     * Replaces a given color's RGB with another color's RGB values in the base texture.
     *
     * @param texture   base texture
     * @param fromColor color to replace, or null for all colors
     * @param toColor   color to replace with
     */
    public ColorReplacer(ResourceLocation texture, @Nullable Color fromColor, Color toColor) {
        this.texture = texture;
        this.fromColor = fromColor != null ? fromColor.getRGB() : null;
        this.toColor = toColor.getRGB();
    }

    @Override
    protected DynamicTexture generate() throws Exception {
        var image = openImage(texture);
        var toColorNoAlpha = argbToAbrg(stripAlpha(toColor));
        var fromColorNoAlpha = fromColor != null ? argbToAbrg(stripAlpha(fromColor)) : null;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int pixel = image.getPixelRGBA(x, y);
                int alpha = (pixel >> 24) & 0xFF;
                // Skip if replacing from color and no color
                if (fromColorNoAlpha != null && fromColorNoAlpha != stripAlpha(pixel)) continue;
                // Skip if pixel is invisible
                if (alpha == 0) continue;
                // Apply color while preserving alpha
                int newPixel = toColorNoAlpha | (alpha << 24);
                image.setPixelRGBA(x, y, newPixel);
            }
        }
        return new DynamicTexture(image);
    }

    private static int stripAlpha(int color) {
        return color & 0x00FFFFFF;
    }

    private static int argbToAbrg(int argb) {
        int alpha = FastColor.ARGB32.alpha(argb);
        int red = FastColor.ARGB32.red(argb);
        int green = FastColor.ARGB32.green(argb);
        int blue = FastColor.ARGB32.blue(argb);
        return FastColor.ABGR32.color(alpha, blue, green, red);
    }

    @Override
    protected String key() {
        var from = fromColor != null ? fromColor : "";
        return keySafe(texture) + "__" + from + "__" + toColor;
    }
}
