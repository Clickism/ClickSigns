package de.clickism.clicksigns.serialization.codec;

import de.clickism.clicksigns.serialization.TagWriter;
import de.clickism.clicksigns.serialization.TagReader;

public interface TagCodec<T> {
    TagWriter.Writer<T> tagWriter();
    TagReader.Reader<T> tagReader();

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
}
