package de.clickism.clicksigns.serialization.codec;

import net.minecraft.network.FriendlyByteBuf;

public interface PacketCodec<T> {
    static <T> PacketCodec<T> of(FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
        return new PacketCodec<>() {
            @Override
            public FriendlyByteBuf.Writer<T> packetWriter() {
                return writer;
            }

            @Override
            public FriendlyByteBuf.Reader<T> packetReader() {
                return reader;
            }
        };
    }

    FriendlyByteBuf.Writer<T> packetWriter();

    FriendlyByteBuf.Reader<T> packetReader();
}
