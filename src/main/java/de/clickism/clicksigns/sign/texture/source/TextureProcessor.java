package de.clickism.clicksigns.sign.texture.source;

import com.google.common.base.CaseFormat;
import de.clickism.clicksigns.serialization.TypeKeyed;
import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.processors.AlphaMask;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public interface TextureProcessor extends TypeKeyed {
    @SuppressWarnings("unchecked")
    static <T extends TextureProcessor> CommonCodec<T> codecOf(String typeKey) {
        return (CommonCodec<T>) switch (typeKey) {
            case Tiler.TYPE -> Tiler.codec();
            case ReplaceColor.TYPE -> ReplaceColor.codec();
            case AlphaMask.TYPE -> AlphaMask.codec();
            default -> throw new UnsupportedOperationException(
                "Serialization of TextureProcessor of type: " + typeKey + " is not supported."
            );
        };
    }

    static CommonCodec<TextureProcessor> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (writer, value) -> {
                    writer.putString("type", value.typeKey());
                    codecOf(value.typeKey()).writeTag(writer, value);
                },
                reader -> {
                    String type = reader.getString("type").orElseThrow();
                    return codecOf(type).readTag(reader);
                }
            ),
            PacketCodec.of(
                (buffer, value) -> {
                    buffer.writeUtf(value.typeKey());
                    codecOf(value.typeKey()).writePacket(buffer, value);
                },
                buffer -> {
                    String type = buffer.readUtf();
                    return codecOf(type).readPacket(buffer);
                }
            )
        );
    }

    /**
     * Processes the given input image and returns the processed image.
     *
     * @param input   the input image to process
     * @param context the texture context containing additional information for processing
     * @return the processed image
     */
    Image process(Image input, TextureContext context);

    /**
     * Returns a unique identity string for this texture processor and its parameters,
     * used for caching.
     * <p>
     * This should also reflect any uses of {@link ColorResolver}, so that the texture cache
     * is only used if the same color resolver is used, or the same relevant colors are produced.
     *
     * @return a unique identity string for this texture processor
     */
    String identity(TextureContext context);

    /**
     * Returns the translation key for this texture processor, used for localization.
     *
     * @return the translation key for this texture processor
     */
    default Component translatedName() {
        var name = getClass().getSimpleName();
        var formatted = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL).convert(name);
        return t("clicksigns.texture.processor." + formatted);
    }
}
