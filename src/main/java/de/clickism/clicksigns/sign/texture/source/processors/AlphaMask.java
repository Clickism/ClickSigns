package de.clickism.clicksigns.sign.texture.source.processors;

import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.texture.source.Image;
import de.clickism.clicksigns.sign.texture.source.TextureContext;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.TextureSource;

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
    public static final String TYPE = "alpha_mask";

    public static CommonCodec<AlphaMask> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (writer, value) -> {
                    var tag = writer.createTag();
                    TextureSource.codec().writeTag(tag, value.mask);
                    writer.putTag("mask", tag);
                },
                reader -> new AlphaMask(
                    TextureSource.codec().readTag(reader.getTag("mask").orElseThrow())
                )
            ),
            PacketCodec.of(
                (buffer, value) -> {
                    TextureSource.codec().writePacket(buffer, value.mask);
                },
                buffer -> {
                    return new AlphaMask(TextureSource.codec().readPacket(buffer));
                }
            )
        );
    }

    @Override
    public Image process(Image input, TextureContext context) {
        var maskImage = mask.resolveImage(context.colorResolver());
        input.forEachPixel((x, y, color) -> {
            int maskColor = maskImage.withinBounds(x, y)
                ? maskImage.pixelAt(x, y)
                : 0; // Transparent if not within bounds
            int maskAlpha = (maskColor >> 24) & 0xFF;
            int colorAlpha = (color >> 24) & 0xFF;
            // Use the minimum so that the mask can only make the pixel more transparent, not more opaque
            int alpha = Math.min(maskAlpha, colorAlpha);
            int newColor = (alpha << 24) | (color & 0xFFFFFF);
            input.setPixelAt(x, y, newColor);
        });
        return input;
    }

    @Override
    public String identity(TextureContext context) {
        return toString();
    }

    @Override
    public String typeKey() {
        return TYPE;
    }
}
