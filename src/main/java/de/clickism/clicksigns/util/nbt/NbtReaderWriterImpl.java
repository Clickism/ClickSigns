package de.clickism.clicksigns.util.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.Optional;

public record NbtReaderWriterImpl(CompoundTag tag) implements NbtReader, NbtWriter {
    @Override
    public void putString(String key, String value) {
        tag.putString(key, value);
    }

    @Override
    public Optional<String> getString(String key) {
        if (!tag.contains(key, Tag.TAG_STRING)) return Optional.empty();
        return Optional.of(tag.getString(key));
    }

    @Override
    public void putInt(String key, int value) {
        tag.putInt(key, value);
    }

    @Override
    public Optional<Integer> getInt(String key) {
        if (!tag.contains(key, Tag.TAG_INT)) return Optional.empty();
        return Optional.of(tag.getInt(key));
    }

    @Override
    public void putFloat(String key, float value) {
        tag.putFloat(key, value);
    }

    @Override
    public Optional<Float> getFloat(String key) {
        if (!tag.contains(key, Tag.TAG_FLOAT)) return Optional.empty();
        return Optional.of(tag.getFloat(key));
    }

    @Override
    public void putDouble(String key, double value) {
        tag.putDouble(key, value);
    }

    @Override
    public Optional<Double> getDouble(String key) {
        if (!tag.contains(key, Tag.TAG_DOUBLE)) return Optional.empty();
        return Optional.of(tag.getDouble(key));
    }

    @Override
    public void putLong(String key, long value) {
        tag.putLong(key, value);
    }

    @Override
    public Optional<Long> getLong(String key) {
        if (!tag.contains(key, Tag.TAG_LONG)) return Optional.empty();
        return Optional.of(tag.getLong(key));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        tag.putBoolean(key, value);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        if (!tag.contains(key, Tag.TAG_BYTE)) return Optional.empty();
        return Optional.of(tag.getBoolean(key));
    }

    @Override
    public <T> void putCollection(String key, Iterable<T> collection, Writer<T> writer) {
        var list = new ListTag();
        for (T item : collection) {
            CompoundTag itemTag = new CompoundTag();
            writer.write(new NbtReaderWriterImpl(itemTag), item);
            list.add(itemTag);
        }
        tag.put(key, list);
    }

    @Override
    public <T> Optional<Collection<T>> getCollection(String key, Reader<T> reader) {
        if (!tag.contains(key, Tag.TAG_LIST)) return Optional.empty();
        var list = tag.getList(key, Tag.TAG_COMPOUND);
        var collection = list.stream()
                .filter(element -> element instanceof CompoundTag)
                .map(element -> reader.read(new NbtReaderWriterImpl((CompoundTag) element)))
                .toList();
        return Optional.of(collection);
    }

    @Override
    public void putCompound(String key, CompoundTag compoundTag) {
        tag.put(key, compoundTag);
    }

    @Override
    public Optional<NbtReader> getCompound(String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) return Optional.empty();
        return Optional.of(new NbtReaderWriterImpl(tag.getCompound(key)));
    }

    @Override
    public NbtWriter createWriter() {
        return new NbtReaderWriterImpl(new CompoundTag());
    }

    @Override
    public CompoundTag asCompoundTag() {
        return tag;
    }
}
