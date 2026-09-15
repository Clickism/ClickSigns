package de.clickism.clicksigns.util.nbt.codec;

import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import net.minecraft.network.FriendlyByteBuf;

public interface CommonCodec<T> extends NbtCodec<T>, PacketCodec<T> {
    default void writeNbt(NbtWriter writer, T value) {
        nbtWriter().write(writer, value);
    }

    default T readNbt(NbtReader reader) {
        return nbtReader().read(reader);
    }

    default void writePacket(FriendlyByteBuf buf, T value) {
        packetWriter().accept(buf, value);
    }

    default T readPacket(FriendlyByteBuf buf) {
        return packetReader().apply(buf);
    }

    static <T> CommonCodec<T> of(
        NbtCodec<T> nbtCodec,
        PacketCodec<T> packetCodec
    ) {
        return new CommonCodec<>() {
            @Override
            public NbtWriter.Writer<T> nbtWriter() {
                return nbtCodec.nbtWriter();
            }

            @Override
            public NbtReader.Reader<T> nbtReader() {
                return nbtCodec.nbtReader();
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
}
