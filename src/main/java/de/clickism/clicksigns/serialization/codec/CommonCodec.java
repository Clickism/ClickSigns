package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.TagReader;
import de.clickism.clicksigns.serialization.TagWriter;
import net.minecraft.network.FriendlyByteBuf;
//? if >= 1.21 {
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.network.codec.StreamDecoder;
//?}
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

            //? if < 1.21 {
            /*@Override
            public FriendlyByteBuf.Writer<T> packetWriter() {
                return packetCodec.packetWriter();
            }

            @Override
            public FriendlyByteBuf.Reader<T> packetReader() {
                return packetCodec.packetReader();
            }
            *///?} elif >= 1.21 {
            public StreamEncoder<FriendlyByteBuf,T> packetWriter() {
                return packetCodec.packetWriter();
            }

            @Override
            public StreamDecoder<FriendlyByteBuf,T> packetReader() {
                return packetCodec.packetReader();
            }
            //?}
        };
    }

    default void writeTag(TagWriter writer, T value) {
        tagWriter().write(writer, value);
    }

    default T readTag(TagReader reader) throws Exception {
        return tagReader().read(reader);
    }

    //? if < 1.21 {
    /*default void writePacket(FriendlyByteBuf buf, T value) {
        packetWriter().accept(buf, value);
    }

    default T readPacket(FriendlyByteBuf buf) {
        return packetReader().apply(buf);
    }
    *///?} elif >= 1.21 {
    default void writePacket(FriendlyByteBuf buf, T value) {
        packetWriter().encode(buf, value);
    }

    default T readPacket(FriendlyByteBuf buf) {
        return packetReader().decode(buf);
    }
    //?}
}
