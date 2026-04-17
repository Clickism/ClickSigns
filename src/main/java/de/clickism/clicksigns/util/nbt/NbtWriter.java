package de.clickism.clicksigns.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public interface NbtWriter {
    void putString(String key, String value);

    void putInt(String key, int value);

    void putFloat(String key, float value);

    void putDouble(String key, double value);

    void putLong(String key, long value);

    void putBoolean(String key, boolean value);

    <T> void putCollection(String key, Iterable<T> collection, Writer<T> writer);

    void putCompound(String key, CompoundTag writer);

    NbtWriter createWriter();

    CompoundTag asCompoundTag();

    default void putResourceLocation(String key, ResourceLocation value) {
        putString(key, value.toString());
    }

    interface Writer<T> {
        void write(NbtWriter nbt, T value);
    }
}
