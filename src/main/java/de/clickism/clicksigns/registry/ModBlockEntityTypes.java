package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.platform.Platform;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class ModBlockEntityTypes {
    public static final Supplier<BlockEntityType<RoadSignBlockEntity>> ROAD_SIGN = Platform.get().registerBlockEntityType(
            "road_sign",
            RoadSignBlockEntity::new,
            ModBlocks.ROAD_SIGN
    );

    public static void initialize() {
        // Empty method to trigger static initializers
    }
}
