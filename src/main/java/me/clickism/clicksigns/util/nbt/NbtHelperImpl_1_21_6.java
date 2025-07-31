/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */
//? if >=1.21.6 {
package me.clickism.clicksigns.util.nbt;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NbtHelperImpl_1_21_6 implements NbtHelper {
    private final @Nullable WriteView writeView;
    private final @Nullable ReadView readView;

    public NbtHelperImpl_1_21_6(@Nullable WriteView writeView, @Nullable ReadView readView) {
        this.writeView = writeView;
        this.readView = readView;
    }

    @Override
    public <T> void put(String key, T value) {
        if (writeView == null) {
            throw new IllegalStateException("WriteView is not available");
        }
        Class<?> clazz = value.getClass();
        if (clazz == String.class) {
            writeView.putString(key, (String) value);
        } else if (clazz == Integer.class) {
            writeView.putInt(key, (Integer) value);
        } else if (clazz == Boolean.class) {
            writeView.putBoolean(key, (Boolean) value);
        } else if (clazz == Double.class) {
            writeView.putDouble(key, (Double) value);
        } else if (clazz == Float.class) {
            writeView.putFloat(key, (Float) value);
        } else if (clazz == Long.class) {
            writeView.putLong(key, (Long) value);
        } else if (clazz == Byte.class) {
            writeView.putByte(key, (Byte) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        if (readView == null) {
            throw new IllegalStateException("ReadView is not available");
        }
        Class<?> clazz = defaultValue.getClass();
        if (clazz == String.class) {
            return (T) readView.getString(key, (String) defaultValue);
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(readView.getInt(key, (Integer) defaultValue));
        } else if (clazz == Boolean.class) {
            return (T) Boolean.valueOf(readView.getBoolean(key, (Boolean) defaultValue));
        } else if (clazz == Double.class) {
            return (T) Double.valueOf(readView.getDouble(key, (Double) defaultValue));
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(readView.getFloat(key, (Float) defaultValue));
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(readView.getLong(key, (Long) defaultValue));
        } else if (clazz == Byte.class) {
            return (T) Byte.valueOf(readView.getByte(key, (Byte) defaultValue));
        }
        throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
    }

    @Override
    public List<String> getStringList(String key) {
        if (readView == null) {
            throw new IllegalStateException("ReadView is not available");
        }
        return readView.getTypedListView(key, Codec.STRING).stream()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void putStringList(String key, List<String> value) {
        if (writeView == null) {
            throw new IllegalStateException("WriteView is not available");
        }
        var listAppender = writeView.getListAppender(key, Codec.STRING);
        value.forEach(listAppender::add);
    }
}
//?}