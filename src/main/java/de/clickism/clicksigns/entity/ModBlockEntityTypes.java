/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.block.ModBlocks;
import net.minecraft.block.entity.BlockEntityType;

//? if forge {
/*import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
*///?} else {
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
//?}

public class ModBlockEntityTypes {
    //? if fabric {
    public static final BlockEntityType<RoadSignBlockEntity> ROAD_SIGN = register(
            "road_sign",
            //? if >=1.20.5 <1.21.2 {
            /*BlockEntityType.Builder
            *///?} else {
            FabricBlockEntityTypeBuilder
            //?}
                    .create(RoadSignBlockEntity::new, ModBlocks.ROAD_SIGN).build()
    );

    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                ClickSigns.identifier(path),
                blockEntityType
        );
    }
    //?}

    //? if forge {
    /*public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ClickSigns.MOD_ID);

    public static final RegistryObject<BlockEntityType<RoadSignBlockEntity>> ROAD_SIGN =
            BLOCK_ENTITIES.register("road_sign",
                    () -> BlockEntityType.Builder.create(RoadSignBlockEntity::new, ModBlocks.ROAD_SIGN.get())
                            .build(null)
            );
    *///?}

    public static void initialize() {
        //? if forge
        /*BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());*/
    }
}
