package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.PacketReader;
import de.clickism.clicksigns.serialization.PacketWriter;
import de.clickism.clicksigns.serialization.TagReader;
import de.clickism.clicksigns.serialization.TagWriter;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Optional;

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
            public PacketWriter<T> packetWriter() {
                return packetCodec.packetWriter();
            }

            @Override
            public PacketReader<T> packetReader() {
                return packetCodec.packetReader();
            }
        };
    }

    default void writeTag(TagWriter writer, T value) {
        tagWriter().write(writer, value);
    }

    default T readTag(TagReader reader) throws Exception {
        return tagReader().read(reader);
    }

    default void writePacket(FriendlyByteBuf buf, T value) {
        packetWriter().write(buf, value);
    }

    default T readPacket(FriendlyByteBuf buf) {
        return packetReader().read(buf);
    }
}
