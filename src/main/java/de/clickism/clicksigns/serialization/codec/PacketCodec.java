package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.PacketReader;
import de.clickism.clicksigns.serialization.PacketWriter;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    static <T> void writeCollection(FriendlyByteBuf buf, Collection<T> collection, PacketWriter<T> writer) {
        buf.writeVarInt(collection.size());
        for (T element : collection) {
            writer.write(buf, element);
        }
    }

    static <T> List<T> readList(FriendlyByteBuf buf, PacketReader<T> reader) {
        int size = buf.readVarInt();
        List<T> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(reader.read(buf));
        }
        return list;
    }
}
