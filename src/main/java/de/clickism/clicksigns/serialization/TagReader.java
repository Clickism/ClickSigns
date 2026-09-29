package de.clickism.clicksigns.serialization;

import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.Optional;

public interface TagReader {
    Result<String> getString(String key);

    Result<Integer> getInt(String key);

    Result<Float> getFloat(String key);

    Result<Double> getDouble(String key);

    Result<Long> getLong(String key);

    Result<Boolean> getBoolean(String key);

    <T> Result<Collection<T>> getCollection(String key, Reader<T> reader);

    Result<TagReader> getTag(String key);

    default Result<Identifier> getIdentifier(String key) {
        return getString(key).map(Identifier::tryParse);
    }

    interface Reader<T> {
        T read(TagReader nbt) throws Exception;

        default T readOrNull(TagReader nbt) {
            try {
                return read(nbt);
            } catch (Exception e) {
                return null;
            }
        }

        default Result<T> readResult(TagReader nbt) {
            try {
                return Result.success(read(nbt));
            } catch (Exception e) {
                return Result.failure(e);
            }
        }
    }
}
