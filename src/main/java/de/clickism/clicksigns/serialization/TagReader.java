package de.clickism.clicksigns.serialization;

import net.minecraft.resources.ResourceLocation;

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

    default Result<ResourceLocation> getResourceLocation(String key) {
        return getString(key).map(ResourceLocation::tryParse);
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
    }
}
