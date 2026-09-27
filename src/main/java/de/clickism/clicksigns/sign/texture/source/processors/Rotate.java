package de.clickism.clicksigns.sign.texture.source.processors;

import de.clickism.clicksigns.serialization.codec.CommonCodec;
import de.clickism.clicksigns.serialization.codec.PacketCodec;
import de.clickism.clicksigns.serialization.codec.TagCodec;
import de.clickism.clicksigns.sign.texture.source.Image;
import de.clickism.clicksigns.sign.texture.source.TextureContext;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;

public record Rotate(
    int degrees
) implements TextureProcessor {
    public static final String TYPE = "rotate";

    public static CommonCodec<Rotate> codec() {
        return CommonCodec.of(
            TagCodec.of(
                (writer, value) -> {
                    writer.putInt("degrees", value.degrees);
                },
                reader -> new Rotate(
                    reader.getInt("degrees").orElseThrow()
                )
            ),
            PacketCodec.of(
                (buffer, rotate) -> {
                    buffer.writeInt(rotate.degrees);
                },
                buffer -> new Rotate(
                    buffer.readInt()
                )
            )
        );
    }

    @Override
    public Image process(Image input, TextureContext context) {
        int inputWidth = input.width();
        int inputHeight = input.height();

        double radians = Math.toRadians(degrees);
        double sin = Math.sin(radians);
        double cos = Math.cos(radians);

        int outputWidth = (int) Math.ceil(
            Math.abs(inputWidth * cos) +
            Math.abs(inputHeight * sin)
        );

        int outputHeight = (int) Math.ceil(
            Math.abs(inputWidth * sin) +
            Math.abs(inputHeight * cos)
        );

        var output = new Image(outputWidth, outputHeight);

        double inputCenterX = (inputWidth - 1) / 2.0;
        double inputCenterY = (inputHeight - 1) / 2.0;
        double outputCenterX = (outputWidth - 1) / 2.0;
        double outputCenterY = (outputHeight - 1) / 2.0;

        output.forEachPixel((x, y, empty) -> {
            // Position relative to output center.
            double dx = x - outputCenterX;
            double dy = y - outputCenterY;

            // Inverse rotation.
            double sourceX = cos * dx + sin * dy + inputCenterX;
            double sourceY = -sin * dx + cos * dy + inputCenterY;

            // Nearest-neighbor sampling.
            int nearestX = (int) Math.round(sourceX);
            int nearestY = (int) Math.round(sourceY);

            // Outside the source image.
            if (nearestX < 0 || nearestX >= inputWidth ||
                nearestY < 0 || nearestY >= inputHeight) {
                return;
            }

            output.setPixelAt(
                x,
                y,
                input.pixelAt(nearestX, nearestY)
            );
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

    public Rotate withDegrees(int degrees) {
        return new Rotate(degrees);
    }
}