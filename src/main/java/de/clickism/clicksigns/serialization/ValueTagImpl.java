package de.clickism.clicksigns.serialization;

//? if >=26.1 {

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

public class ValueTagImpl {
    private ValueTagImpl() {
    }

    public static ValueTagImpl.Writer writer(ValueOutput output) {
        return new Writer(output);
    }

    public static ValueTagImpl.Reader reader(ValueInput value) {
        return new Reader(value);
    }

    public record Writer(ValueOutput output) implements TagWriter {
        @Override
        public void putString(String key, String value) {
            output.putString(key, value);
        }

        @Override
        public void putInt(String key, int value) {
            output.putInt(key, value);
        }

        @Override
        public void putFloat(String key, float value) {
            output.putFloat(key, value);
        }

        @Override
        public void putDouble(String key, double value) {
            output.putDouble(key, value);
        }

        @Override
        public void putLong(String key, long value) {
            output.putLong(key, value);
        }

        @Override
        public void putBoolean(String key, boolean value) {
            output.putBoolean(key, value);
        }

        @Override
        public <T> void putCollection(String key, @Nullable Iterable<T> collection, Writer<T> writer) {
            if (collection == null) {
                output.discard(key);
                return;
            }
            var list = output.childrenList(key);
            collection.forEach(element -> {
                var elementOutput = list.addChild();
                writer.write(new ValueTagImpl.Writer(elementOutput), element);
            });
        }

        @Override
        public void putTag(String key, @Nullable Consumer<TagWriter> writer) {
            if (writer == null) {
                output.discard(key);
                return;
            }
            var childOutput = output.child(key);
            writer.accept(new ValueTagImpl.Writer(childOutput));
        }
    }

    public record Reader(ValueInput value) implements TagReader {
        @Override
        public Result<String> getString(String key) {
            return Result.ofOptional(value.getString(key), "Key '" + key + "' is not a string or does not exist.");
        }

        @Override
        public Result<Integer> getInt(String key) {
            return Result.ofOptional(value.getInt(key), "Key '" + key + "' is not an integer or does not exist.");
        }

        @Override
        public Result<Float> getFloat(String key) {
            if (!value.contains(key)) {
                return Result.failure("Key '" + key + "' does not exist.");
            }
            return Result.success(value.getFloatOr(key, 0f));
        }

        @Override
        public Result<Double> getDouble(String key) {
            if (!value.contains(key)) {
                return Result.failure("Key '" + key + "' does not exist.");
            }
            return Result.success(value.getDoubleOr(key, 0d));
        }

        @Override
        public Result<Long> getLong(String key) {
            return Result.ofOptional(value.getLong(key), "Key '" + key + "' is not a long or does not exist.");
        }

        @Override
        public Result<Boolean> getBoolean(String key) {
            if (!value.contains(key)) {
                return Result.failure("Key '" + key + "' does not exist.");
            }
            return Result.success(value.getBooleanOr(key, false));
        }

        @Override
        public <T> Result<Collection<T>> getCollection(String key, Reader<T> reader) {
            if (!value.contains(key)) {
                return Result.failure("Key '" + key + "' does not exist.");
            }
            var list = value.childrenListOrEmpty(key);
            var values = list.stream()
                .map(input -> {
                    var result = reader.readResult(reader(input));
                    if (result.isFailure()) {
                        throw new RuntimeException("Failed to read collection element: " + result.exception().getMessage());
                    }
                    return result.orElseThrow();
                })
                .toList();
            return Result.success(values);
        }

        @Override
        public Result<TagReader> getTag(String key) {
            if (!value.contains(key)) {
                return Result.failure("Key '" + key + "' does not exist.");
            }
            var child = value.childOrEmpty(key);
            return Result.success(reader(child));
        }
    }
}

//?}
