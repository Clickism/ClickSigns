/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.item;

import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

public class ModItemGroups {
    public static void registerItemGroups() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register((entries) -> {
            entries.add(ModBlocks.ROAD_SIGN);
        });
    }
}
