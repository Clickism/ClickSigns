package de.clickism.clicksigns.block

import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING

abstract class HorizontalFacingBlockWithEntity(properties: Properties) : BaseEntityBlock(properties) {
    override fun rotate(
        state: BlockState,
        rotation: Rotation
    ): BlockState? {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING)))
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState? {
        return state.rotate(mirror.getRotation(state.getValue(HORIZONTAL_FACING)))
    }
}