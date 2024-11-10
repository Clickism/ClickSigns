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
