package de.clickism.clicksigns.util.nbt.codec;

import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;

public interface NbtCodec<T> {
    NbtWriter.Writer<T> nbtWriter();
    NbtReader.Reader<T> nbtReader();

    static <T> NbtCodec<T> of(NbtWriter.Writer<T> writer, NbtReader.Reader<T> reader) {
        return new NbtCodec<>() {
            @Override
            public NbtWriter.Writer<T> nbtWriter() {
                return writer;
            }

            @Override
            public NbtReader.Reader<T> nbtReader() {
                return reader;
            }
        };
    }
}
