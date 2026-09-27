package de.clickism.clicksigns.serialization.codec;

import net.minecraft.network.FriendlyByteBuf;
//? if >= 1.21 {
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamEncoder;
//?}

public interface PacketCodec<T> {
    //? if < 1.21 {
    /*static <T> PacketCodec<T> of(FriendlyByteBuf.Writer<T> writer, FriendlyByteBuf.Reader<T> reader) {
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
    *///?} elif >= 1.21 {
    static <T> PacketCodec<T> of(StreamEncoder<FriendlyByteBuf,T> writer, StreamDecoder<FriendlyByteBuf, T> reader) {
        return new PacketCodec<>() {
            @Override
            public StreamEncoder<FriendlyByteBuf,T> packetWriter() {
                return writer;
            }

            @Override
            public StreamDecoder<FriendlyByteBuf,T> packetReader() {
                return reader;
            }
        };
    }

    StreamEncoder<FriendlyByteBuf,T> packetWriter();

    StreamDecoder<FriendlyByteBuf,T> packetReader();
    //?}
}
