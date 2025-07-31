/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */
//? if >=1.21.5 <1.21.6 {
/*package me.clickism.clicksigns.util.nbt;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class NbtHelperImpl_1_21_5 implements NbtHelper {
    private final NbtCompound nbt;

    public NbtHelperImpl_1_21_5(NbtCompound nbt) {
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
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        if (defaultValue instanceof String) {
            return (T) nbt.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf(nbt.getInt(key, (Integer) defaultValue));
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf(nbt.getBoolean(key, (Boolean) defaultValue));
        } else if (defaultValue instanceof Double) {
            return (T) Double.valueOf(nbt.getDouble(key, (Double) defaultValue));
        } else if (defaultValue instanceof Float) {
            return (T) Float.valueOf(nbt.getFloat(key, (Float) defaultValue));
        } else if (defaultValue instanceof Long) {
            return (T) Long.valueOf(nbt.getLong(key, (Long) defaultValue));
        } else if (defaultValue instanceof Byte) {
            return (T) Byte.valueOf(nbt.getByte(key, (Byte) defaultValue));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + defaultValue.getClass().getName());
        }
    }

    @Override
    public List<String> getStringList(String key) {
        return nbt.getListOrEmpty(key)
                .stream()
                .map(NbtElement::asString)
                .map(Optional::orElseThrow)
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
*/