package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.TagReader;
import de.clickism.clicksigns.serialization.TagWriter;
import net.minecraft.network.FriendlyByteBuf;

public interface CommonCodec<T> extends TagCodec<T>, PacketCodec<T> {
    static <T> CommonCodec<T> of(
        TagCodec<T> nbtCodec,
        PacketCodec<T> packetCodec
    ) {
        return new CommonCodec<>() {
            @Override
            public TagWriter.Writer<T> tagWriter() {
                return nbtCodec.tagWriter();
            }

            @Override
            public TagReader.Reader<T> tagReader() {
                return nbtCodec.tagReader();
            }

            @Override
            public FriendlyByteBuf.Writer<T> packetWriter() {
                return packetCodec.packetWriter();
            }

            @Override
            public FriendlyByteBuf.Reader<T> packetReader() {
                return packetCodec.packetReader();
            }
        };
    }

    default void writeTag(TagWriter writer, T value) {
        tagWriter().write(writer, value);
    }

    default T readTag(TagReader reader) {
        return tagReader().read(reader);
    }

    default void writePacket(FriendlyByteBuf buf, T value) {
        packetWriter().accept(buf, value);
    }

    default T readPacket(FriendlyByteBuf buf) {
        return packetReader().apply(buf);
    }
}
