package me.clickism.clicksigns.block;

import me.clickism.clicksigns.ClickSigns;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlocks {

    public static final Block ROAD_SIGN = registerBlock("road_sign",
            new RoadSignBlock(AbstractBlock.Settings.copy(Blocks.STONE)));

    private static Block registerBlock(String id, Block block) {
        registerBlockItem(id, block);
        return Registry.register(Registries.BLOCK, ClickSigns.identifier(id), block);
    }

    private static void registerBlockItem(String id, Block block) {
        Registry.register(
                Registries.ITEM,
                ClickSigns.identifier(id),
                new BlockItem(block, new Item.Settings())
        );
    }

    public static void registerBlocks() {
    }
}
