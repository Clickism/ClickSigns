package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.PacketReader;
import de.clickism.clicksigns.serialization.PacketWriter;

public interface PacketCodec<T> {
    static <T> PacketCodec<T> of(PacketWriter<T> writer, PacketReader<T> reader) {
        return new PacketCodec<>() {
            @Override
            public PacketWriter<T> packetWriter() {
                return writer;
            }

            @Override
            public PacketReader<T> packetReader() {
                return reader;
            }
        };
    }

    PacketWriter<T> packetWriter();

    PacketReader<T> packetReader();
}
