package de.clickism.clicksigns.serialization;

import de.clickism.clicksigns.ClickSigns;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Collection;
import java.util.Objects;

/**
 * Implementation of TagReader and TagWriter using NBT's.
 *
 * @param tag
 */
public record NbtTagImpl(CompoundTag tag) implements TagReader, TagWriter {
    public static NbtTagImpl empty() {
        return new NbtTagImpl(new CompoundTag());
    }

    @Override
    public void putString(String key, String value) {
        if (value == null) {
            tag.remove(key);
            return;
        }
        tag.putString(key, value);
    }

    @Override
    public Result<String> getString(String key) {
        if (!tag.contains(key, Tag.TAG_STRING))
            return Result.failure("Key '" + key + "' is not a string or does not exist.");
        return Result.success(tag.getString(key));
    }

    @Override
    public void putInt(String key, int value) {
        tag.putInt(key, value);
    }

    @Override
    public Result<Integer> getInt(String key) {
        if (!tag.contains(key, Tag.TAG_INT))
            return Result.failure("Key '" + key + "' is not an integer or does not exist.");
        return Result.success(tag.getInt(key));
    }

    @Override
    public void putFloat(String key, float value) {
        tag.putFloat(key, value);
    }

    @Override
    public Result<Float> getFloat(String key) {
        if (!tag.contains(key, Tag.TAG_FLOAT))
            return Result.failure("Key '" + key + "' is not a float or does not exist.");
        return Result.success(tag.getFloat(key));
    }

    @Override
    public void putDouble(String key, double value) {
        tag.putDouble(key, value);
    }

    @Override
    public Result<Double> getDouble(String key) {
        if (!tag.contains(key, Tag.TAG_DOUBLE))
            return Result.failure("Key '" + key + "' is not a double or does not exist.");
        return Result.success(tag.getDouble(key));
    }

    @Override
    public void putLong(String key, long value) {
        tag.putLong(key, value);
    }

    @Override
    public Result<Long> getLong(String key) {
        if (!tag.contains(key, Tag.TAG_LONG))
            return Result.failure("Key '" + key + "' is not a long or does not exist.");
        return Result.success(tag.getLong(key));
    }

    @Override
    public void putBoolean(String key, boolean value) {
        tag.putBoolean(key, value);
    }

    @Override
    public Result<Boolean> getBoolean(String key) {
        if (!tag.contains(key, Tag.TAG_BYTE))
            return Result.failure("Key '" + key + "' is not a boolean or does not exist.");
        return Result.success(tag.getBoolean(key));
    }

    @Override
    public <T> void putCollection(String key, Iterable<T> collection, Writer<T> writer) {
        if (collection == null) {
            tag.remove(key);
            return;
        }
        var list = new ListTag();
        for (T item : collection) {
            CompoundTag itemTag = new CompoundTag();
            writer.write(new NbtTagImpl(itemTag), item);
            list.add(itemTag);
        }
        tag.put(key, list);
    }

    @Override
    public <T> Result<Collection<T>> getCollection(String key, Reader<T> reader) {
        if (!tag.contains(key, Tag.TAG_LIST))
            return Result.failure("Key '" + key + "' is not a list or does not exist.");
        var list = tag.getList(key, Tag.TAG_COMPOUND);
        var collection = list.stream()
            .filter(element -> element instanceof CompoundTag)
            .map(element -> {
                try {
                    return reader.read(new NbtTagImpl((CompoundTag) element));
                } catch (Exception e) {
                    ClickSigns.LOGGER.error("Failed to read item from collection for key '{}'", key, e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
        return Result.success(collection);
    }

    @Override
    public void putTag(String key, TagWriter tag) {
        if (tag == null) {
            this.tag.remove(key);
            return;
        }
        if (!(tag instanceof NbtTagImpl nbtTag)) {
            throw new IllegalArgumentException("compoundTag must be an instance of NbtTagImpl");
        }
        this.tag.put(key, nbtTag.tag);
    }

    @Override
    public Result<TagReader> getTag(String key) {
        if (!tag.contains(key, Tag.TAG_COMPOUND))
            return Result.failure("Key '" + key + "' is not a compound tag or does not exist.");
        return Result.success(new NbtTagImpl(tag.getCompound(key)));
    }

    @Override
    public TagWriter createTag() {
        return empty();
    }
}
