package de.clickism.clicksigns.util.nbt;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Optional;

public interface NbtReader {
    Optional<String> getString(String key);

    Optional<Integer> getInt(String key);

    Optional<Float> getFloat(String key);

    Optional<Double> getDouble(String key);

    Optional<Long> getLong(String key);

    Optional<Boolean> getBoolean(String key);

    <T> Optional<Collection<T>> getCollection(String key, Reader<T> reader);

    Optional<NbtReader> getCompound(String key);

    default Optional<ResourceLocation> getResourceLocation(String key) {
        return getString(key).map(ResourceLocation::tryParse);
    }

    interface Reader<T> {
        T read(NbtReader nbt);
    }
}
