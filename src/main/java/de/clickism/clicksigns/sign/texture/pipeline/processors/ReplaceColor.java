package de.clickism.clicksigns.sign.texture.pipeline.processors;

import de.clickism.clicksigns.sign.texture.pipeline.Image;
import de.clickism.clicksigns.sign.texture.pipeline.TextureContext;
import de.clickism.clicksigns.sign.texture.pipeline.TextureProcessor;
import org.jetbrains.annotations.Nullable;

/**
 * Replaces a specific color in the input image with another color, whilst
 * ignoring the alpha channel.
 * <p>
 * If <code>fromColor</code> is null, <strong>all</strong> colors will be replaced.
 *
 * @param fromColor
 * @param toColor
 */
public record ReplaceColor(
    @Nullable String fromColor,
    String toColor
) implements TextureProcessor {
    @Override
    public Image process(Image input, TextureContext context) {
        boolean replaceAll = fromColor == null;
        // Convert colors to integers
        int fromColorArgb = context.colorResolver().resolveInt(fromColor);
        int toColorArgb = context.colorResolver().resolveInt(toColor);
        input.forEachPixel((x, y, color) -> {
            if (replaceAll || color == fromColorArgb) {
                int alpha = (color >> 24) & 0xFF;
                int newColor = (alpha << 24) | (toColorArgb & 0xFFFFFF);
                input.setPixelAt(x, y, newColor);
            }
        });
        return input;
    }

    @Override
    public String identity() {
        return this.toString();
    }
}
