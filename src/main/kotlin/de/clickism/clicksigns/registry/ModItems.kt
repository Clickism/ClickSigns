package de.clickism.clicksigns.registry

import de.clickism.clicksigns.platform.Platform
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour

object ModItems {
    val ROAD_SIGN = Platform.registerBlockWithItem(
        "road_sign",
        BlockBehaviour.Properties.ofFullCopy(Blocks.STONE)
    ) { blockSettings ->
        Block(blockSettings)
    }

    fun initialize() {

    }
}