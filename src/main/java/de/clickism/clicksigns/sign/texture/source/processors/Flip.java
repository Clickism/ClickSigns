package de.clickism.clicksigns.sign.texture.source.processors;

import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.texture.source.Image;
import de.clickism.clicksigns.sign.texture.source.TextureContext;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;

/**
 * Flips a given image horizontally and/or vertically based on the specified flip flags.
 *
 * @param flipX whether to flip the image horizontally
 * @param flipY whether to flip the image vertically
 */
public record Flip(
    boolean flipX,
    boolean flipY
) implements TextureProcessor {
    public static final String TYPE = "flip";

    public static CommonCodec<Flip> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (writer, value) -> {
                    writer.putBoolean("flipX", value.flipX);
                    writer.putBoolean("flipY", value.flipY);
                },
                reader -> new Flip(
                    reader.getBoolean("flipX").orElseThrow(),
                    reader.getBoolean("flipY").orElseThrow()
                )
            ),
            PacketCodec.of(
                (buffer, value) -> {
                    buffer.writeBoolean(value.flipX);
                    buffer.writeBoolean(value.flipY);
                },
                buffer -> new Flip(
                    buffer.readBoolean(),
                    buffer.readBoolean()
                )
            )
        );
    }

    @Override
    public Image process(Image input, TextureContext context) {
        int inputWidth = input.width();
        int inputHeight = input.height();
        var output = new Image(inputWidth, inputHeight);
        output.forEachPixel((x, y, empty) -> {
            int sourceX = flipX
                ? inputWidth - 1 - x
                : x;
            int sourceY = flipY
                ? inputHeight - 1 - y
                : y;
            output.setPixelAt(x, y, input.pixelAt(sourceX, sourceY));
        });
        return output;
    }

    @Override
    public String identity(TextureContext context) {
        return this.toString();
    }

    @Override
    public String typeKey() {
        return TYPE;
    }

    public Flip withFlipX(boolean flipX) {
        return new Flip(flipX, this.flipY);
    }

    public Flip withFlipY(boolean flipY) {
        return new Flip(this.flipX, flipY);
    }
}
