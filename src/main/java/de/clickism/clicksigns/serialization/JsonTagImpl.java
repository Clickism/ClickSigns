package de.clickism.clicksigns.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

/**
 * Implementation of TagReader and TagWriter using GSON's JsonObject.
 */
public record JsonTagImpl(JsonObject jsonObject) implements TagReader, TagWriter {
    @Override
    public Optional<String> getString(String key) {
        JsonElement element = jsonObject.get(key);
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            return Optional.empty();
        }
        return Optional.of(element.getAsString());
    }

    @Override
    public Optional<Integer> getInt(String key) {
        JsonElement element = jsonObject.get(key);
        if (!isNumber(element)) {
            return Optional.empty();
        }
        return Optional.of(element.getAsInt());
    }

    @Override
    public Optional<Float> getFloat(String key) {
        JsonElement element = jsonObject.get(key);
        if (!isNumber(element)) {
            return Optional.empty();
        }
        return Optional.of(element.getAsFloat());
    }

    @Override
    public Optional<Double> getDouble(String key) {
        JsonElement element = jsonObject.get(key);
        if (!isNumber(element)) {
            return Optional.empty();
        }
        return Optional.of(element.getAsDouble());
    }

    @Override
    public Optional<Long> getLong(String key) {
        JsonElement element = jsonObject.get(key);
        if (!isNumber(element)) {
            return Optional.empty();
        }
        return Optional.of(element.getAsLong());
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        JsonElement element = jsonObject.get(key);
        if (element == null || !element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) {
            return Optional.empty();
        }
        return Optional.of(element.getAsBoolean());
    }

    @Override
    public <T> Optional<Collection<T>> getCollection(String key, Reader<T> reader) {
        JsonElement element = jsonObject.get(key);
        if (element == null || !element.isJsonArray()) {
            return Optional.empty();
        }

        JsonArray array = element.getAsJsonArray();

        var collection = array.asList().stream()
            .filter(JsonElement::isJsonObject)
            .map(item -> reader.read(new JsonTagImpl(item.getAsJsonObject())))
            .toList();

        return Optional.of(collection);
    }

    @Override
    public Optional<TagReader> getTag(String key) {
        JsonElement element = jsonObject.get(key);
        if (element == null || !element.isJsonObject()) {
            return Optional.empty();
        }
        return Optional.of(new JsonTagImpl(element.getAsJsonObject()));
    }

    @Override
    public void putString(String key, String value) {
        if (value == null) {
            jsonObject.remove(key);
            return;
        }
        jsonObject.addProperty(key, value);
    }

    @Override
    public void putInt(String key, int value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void putFloat(String key, float value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void putDouble(String key, double value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void putLong(String key, long value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public void putBoolean(String key, boolean value) {
        jsonObject.addProperty(key, value);
    }

    @Override
    public <T> void putCollection(
        String key,
        @Nullable Iterable<T> collection,
        Writer<T> writer
    ) {
        if (collection == null) {
            jsonObject.remove(key);
            return;
        }

        var array = new JsonArray();

        for (T item : collection) {
            var itemObject = new JsonObject();
            writer.write(new JsonTagImpl(itemObject), item);
            array.add(itemObject);
        }

        jsonObject.add(key, array);
    }

    @Override
    public void putTag(String key, @Nullable TagWriter writer) {
        if (writer == null) {
            jsonObject.remove(key);
            return;
        }

        if (!(writer instanceof JsonTagImpl jsonTag)) {
            throw new IllegalArgumentException("writer must be an instance of JsonTagImpl");
        }

        jsonObject.add(key, jsonTag.jsonObject);
    }

    @Override
    public TagWriter createTag() {
        return new JsonTagImpl(new JsonObject());
    }

    /**
     * Checks if the given JsonElement is a number (int, float, double, long).
     *
     * @param element the JsonElement to check
     * @return true if the element is a number, false otherwise
     */
    private boolean isNumber(@Nullable JsonElement element) {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }
}