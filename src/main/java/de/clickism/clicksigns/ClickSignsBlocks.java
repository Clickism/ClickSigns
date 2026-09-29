package de.clickism.clicksigns;

import de.clickism.clicksigns.block.RoadSignBlock;
import de.clickism.clicksigns.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

/**
 * Mod blocks registry
 */
public class ClickSignsBlocks {
    /**
     * Road sign block
     */
    public static final Supplier<Block> ROAD_SIGN = Platform.get().registerBlockWithItem(
        "road_sign",
        //~ if >=1.21.1 'copy' -> 'ofFullCopy'
        BlockBehaviour.Properties.ofFullCopy(Blocks.STONE),
        RoadSignBlock::new
    );

    public static void initialize() {
        // Empty method to trigger static initializers
        Platform.get().addItemToCreativeTab(CreativeModeTabs.FUNCTIONAL_BLOCKS, () -> ROAD_SIGN.get().asItem());
    }
}
