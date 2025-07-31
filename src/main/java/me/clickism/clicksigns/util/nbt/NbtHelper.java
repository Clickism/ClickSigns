/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.util.nbt;

//? if >=1.21.6 {
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;
import java.util.List;
//?} else {
/*import net.minecraft.nbt.NbtCompound;

import java.util.List;
*///?}

public interface NbtHelper {
    <T> void put(String key, T value);

    void putStringList(String key, List<String> value);

    <T> T getOrDefault(String key, T defaultValue);

    List<String> getStringList(String key);

    //? if >=1.21.6 {
    static NbtHelper create(@Nullable WriteView writeView, @Nullable ReadView readView) {
        return new NbtHelperImpl_1_21_6(writeView, readView);

    }
    //?} else {
    /*static NbtHelper create(NbtCompound nbt) {
        //? if >=1.21.5 {
        return new NbtHelperImpl_1_21_5(nbt);
        //?} else
        /^return new NbtHelperImpl_1_20_1(nbt);^/
    }
    *///?}
}
