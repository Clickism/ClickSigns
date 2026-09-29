package de.clickism.clicksigns.serialization;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface TagWriter {
    void putString(String key, String value);

    void putInt(String key, int value);

    void putFloat(String key, float value);

    void putDouble(String key, double value);

    void putLong(String key, long value);

    void putBoolean(String key, boolean value);

    <T> void putCollection(String key, @Nullable Iterable<T> collection, Writer<T> writer);

    void putTag(String key, @Nullable Consumer<TagWriter> writer);

    default void putIdentifier(String key, Identifier value) {
        putString(key, value.toString());
    }

    default void delete(String key) {
        putTag(key, null);
    }

    interface Writer<T> {
        void write(TagWriter nbt, T value);
    }
}
