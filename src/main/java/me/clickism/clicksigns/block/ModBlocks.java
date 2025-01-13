package me.clickism.clicksigns.block;

import me.clickism.clicksigns.ClickSigns;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ModBlocks {

    public static final Block ROAD_SIGN = registerBlock("road_sign",
            new RoadSignBlock(AbstractBlock.Settings.copy(Blocks.STONE)
                    //? if >=1.21.2
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, ClickSigns.identifier("road_sign")))
            ));

    private static Block registerBlock(String id, Block block) {
        registerBlockItem(id, block);
        return Registry.register(Registries.BLOCK, ClickSigns.identifier(id), block);
    }

    private static void registerBlockItem(String id, Block block) {
        Registry.register(
                Registries.ITEM,
                ClickSigns.identifier(id),
                new BlockItem(block, new Item.Settings()
                    //? if >=1.21.2
                    .registryKey(RegistryKey.of(RegistryKeys.ITEM, ClickSigns.identifier(id)))
                )
        );
    }

    public static void registerBlocks() {
    }
}
