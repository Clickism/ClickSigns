package me.clickism.clicksigns.item;

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class ModItemGroups {
    public static final ItemGroup SIGN_BLOCKS = Registry.register(
            Registries.ITEM_GROUP, ClickSigns.identifier("sign_blocks_group"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModBlocks.ROAD_SIGN))
                    .displayName(Text.translatable("clicksigns.text.road_signs"))
                    .entries(((displayContext, entries) -> {
                        entries.add(ModBlocks.ROAD_SIGN);
                    }))
                    .build()
    );

    public static void registerItemGroups() {
    }
}
