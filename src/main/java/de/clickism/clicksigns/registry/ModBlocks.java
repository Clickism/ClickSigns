package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.block.RoadSignBlock;
import de.clickism.clicksigns.platform.Platform;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * Mod blocks registry
 */
public class ModBlocks {
    /**
     * Road sign block
     */
    public static final Supplier<Block> ROAD_SIGN = Platform.get().registerBlockWithItem(
            "road_sign",
            BlockBehaviour.Properties.copy(Blocks.STONE),
            RoadSignBlock::new
    );

    public static void initialize() {
        // Empty method to trigger static initializers
    }
}
