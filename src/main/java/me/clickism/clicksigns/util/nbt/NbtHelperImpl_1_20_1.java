/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */
//? if >=1.20.1 <1.21.5 {
/*package me.clickism.clicksigns.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NbtHelperImpl_1_20_1 implements NbtHelper {
    private final NbtCompound nbt;

    public NbtHelperImpl_1_20_1(NbtCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public <T> void put(String key, T value) {
        if (value instanceof String) {
            nbt.putString(key, (String) value);
        } else if (value instanceof Integer) {
            nbt.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            nbt.putBoolean(key, (Boolean) value);
        } else if (value instanceof Double) {
            nbt.putDouble(key, (Double) value);
        } else if (value instanceof Float) {
            nbt.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            nbt.putLong(key, (Long) value);
        } else if (value instanceof Byte) {
            nbt.putByte(key, (Byte) value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
        }
    }

    @Override
    public <T> T getOrDefault(String key, T defaultValue) {
        if (nbt.contains(key)) {
            return get(key, defaultValue.getClass());
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String key, Class<?> clazz) {
        if (clazz == String.class) {
            return (T) nbt.getString(key);
        } else if (clazz == Integer.class) {
            return (T) Integer.valueOf(nbt.getInt(key));
        } else if (clazz == Boolean.class) {
            return (T) Boolean.valueOf(nbt.getBoolean(key));
        } else if (clazz == Double.class) {
            return (T) Double.valueOf(nbt.getDouble(key));
        } else if (clazz == Float.class) {
            return (T) Float.valueOf(nbt.getFloat(key));
        } else if (clazz == Long.class) {
            return (T) Long.valueOf(nbt.getLong(key));
        } else if (clazz == Byte.class) {
            return (T) Byte.valueOf(nbt.getByte(key));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
        }
    }

    @Override
    public List<String> getStringList(String key) {
        return nbt.getList(key, NbtElement.STRING_TYPE)
                .stream()
                .map(NbtElement::asString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void putStringList(String key, List<String> value) {
        NbtList nbtList = new NbtList();
        nbtList.addAll(value.stream()
                .map(NbtString::of)
                .toList());
        nbt.put(key, nbtList);
    }
}
*///?}