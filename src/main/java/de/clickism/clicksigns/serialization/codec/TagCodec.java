package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.TagReader;
import de.clickism.clicksigns.serialization.TagWriter;

public interface TagCodec<T> {
    static <T> TagCodec<T> of(TagWriter.Writer<T> writer, TagReader.Reader<T> reader) {
        return new TagCodec<>() {
            @Override
            public TagWriter.Writer<T> tagWriter() {
                return writer;
            }

            @Override
            public TagReader.Reader<T> tagReader() {
                return reader;
            }
        };
    }

    TagWriter.Writer<T> tagWriter();

    TagReader.Reader<T> tagReader();
}
