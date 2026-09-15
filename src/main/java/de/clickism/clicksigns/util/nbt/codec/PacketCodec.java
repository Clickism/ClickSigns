package de.clickism.clicksigns.util.nbt.codec;

import net.minecraft.network.FriendlyByteBuf;

public interface PacketCodec<T> {
    FriendlyByteBuf.Writer<T> packetWriter();

    FriendlyByteBuf.Reader<T> packetReader();

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
}
