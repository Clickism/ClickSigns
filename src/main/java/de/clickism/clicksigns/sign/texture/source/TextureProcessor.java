package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.processors.AlphaMask;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.util.nbt.TypeKeyed;
import de.clickism.clicksigns.util.nbt.codec.CommonCodec;
import de.clickism.clicksigns.util.nbt.codec.NbtCodec;
import de.clickism.clicksigns.util.nbt.codec.PacketCodec;

public interface TextureProcessor extends TypeKeyed {
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
            NbtCodec.of(
                (writer, value) -> {
                    writer.putString("type", value.typeKey());
                    codecOf(value.typeKey()).writeNbt(writer, value);
                },
                reader -> {
                    String type = reader.getString("type").orElseThrow();
                    return codecOf(type).readNbt(reader);
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
}
