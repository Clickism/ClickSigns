package me.clickism.clicksigns.entity;

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntityTypes {
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

    public static void initialize() {
    }
}
