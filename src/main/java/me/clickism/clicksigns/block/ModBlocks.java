/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

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
//? if forge {
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
//?}

public class ModBlocks {

    //? if fabric {
    /*public static final Block ROAD_SIGN = registerBlock("road_sign",
            new RoadSignBlock(AbstractBlock.Settings.copy(Blocks.STONE)
                    //? if >=1.21.2
                    /^.registryKey(RegistryKey.of(RegistryKeys.BLOCK, ClickSigns.identifier("road_sign")))^/
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
                    /^.registryKey(RegistryKey.of(RegistryKeys.ITEM, ClickSigns.identifier(id)))^/
                )
        );
    }
    *///?}

    //? if forge {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ClickSigns.MOD_ID);

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ClickSigns.MOD_ID);

    public static final RegistryObject<Block> ROAD_SIGN = BLOCKS.register("road_sign",
            () -> new RoadSignBlock(AbstractBlock.Settings.copy(Blocks.STONE))
    );

    public static final RegistryObject<Item> ROAD_SIGN_ITEM = ITEMS.register("road_sign",
            () -> new BlockItem(ROAD_SIGN.get(), new Item.Settings())
    );
    //?}

    public static void registerBlocks() {
        //? if forge {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        //?}
    }
}
