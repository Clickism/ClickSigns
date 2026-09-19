package de.clickism.clicksigns.sign.texture.source.processors;

import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.texture.source.Image;
import de.clickism.clicksigns.sign.texture.source.TextureContext;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import net.minecraft.network.FriendlyByteBuf;
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
    public static final String TYPE = "replaceColor";

    public static CommonCodec<ReplaceColor> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (writer, value) -> {
                    writer.putString("toColor", value.toColor);
                    writer.putString("fromColor", value.fromColor);
                },
                reader -> new ReplaceColor(
                    reader.getString("fromColor").orElse(null),
                    reader.getString("toColor").orElseThrow()
                )
            ),
            PacketCodec.of(
                (buffer, value) -> {
                    buffer.writeNullable(value.fromColor, FriendlyByteBuf::writeUtf);
                    buffer.writeUtf(value.toColor);
                },
                buffer -> new ReplaceColor(
                    buffer.readNullable(FriendlyByteBuf::readUtf),
                    buffer.readUtf()
                )
            )
        );
    }

    @Override
    public Image process(Image input, TextureContext context) {
        boolean replaceAll = fromColor == null || fromColor.isEmpty();
        // Convert colors to integers
        int fromColorArgb = context.colorResolver().resolve(fromColor);
        int toColorArgb = context.colorResolver().resolve(toColor);
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
    public String identity(TextureContext context) {
        var from = context.colorResolver().resolve(fromColor);
        var to = context.colorResolver().resolve(toColor);
        return "ReplaceColor[from=" + from + ", to=" + to + "]";
    }

    @Override
    public String typeKey() {
        return TYPE;
    }

    /**
     * Creates a new ReplaceColor instance with the specified fromColor.
     *
     * @param fromColor the new fromColor value, or null to replace all colors
     * @return a new ReplaceColor instance with the updated fromColor
     */
    public ReplaceColor withFromColor(@Nullable String fromColor) {
        return new ReplaceColor(fromColor, this.toColor);
    }

    /**
     * Creates a new ReplaceColor instance with the specified toColor.
     *
     * @param toColor the new toColor value
     * @return a new ReplaceColor instance with the updated toColor
     */
    public ReplaceColor withToColor(String toColor) {
        return new ReplaceColor(this.fromColor, toColor);
    }
}
