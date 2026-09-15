package de.clickism.clicksigns.sign.texture.pipeline.processors;

import de.clickism.clicksigns.sign.texture.pipeline.Image;
import de.clickism.clicksigns.sign.texture.pipeline.TextureContext;
import de.clickism.clicksigns.sign.texture.pipeline.TextureProcessor;
import de.clickism.clicksigns.sign.texture.pipeline.TextureSource;

/**
 * Applies an alpha mask to the input image using the specified mask texture.
 * <p>
 * The alpha channel of the mask texture is used to determine the transparency of each pixel in the input image.
 *
 * @param mask the texture source representing the alpha mask
 */
public record AlphaMask(
    TextureSource mask
) implements TextureProcessor {

    @Override
    public Image process(Image input, TextureContext context) {
        var maskImage = mask.resolveImage(context.colorResolver());
        input.forEachPixel((x, y, color) -> {
            int maskColor = maskImage.withinBounds(x, y)
                ? maskImage.pixelAt(x, y)
                : 0; // Transparent if not within bounds
            int alpha = (maskColor >> 24) & 0xFF;
            int newColor = (alpha << 24) | (color & 0xFFFFFF);
            input.setPixelAt(x, y, newColor);
        });
        return input;
    }

    @Override
    public String identity() {
        return toString();
    }
}
