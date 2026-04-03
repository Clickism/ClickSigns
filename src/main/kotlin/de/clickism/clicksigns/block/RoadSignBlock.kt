package de.clickism.clicksigns.block

import de.clickism.clicksigns.entity.RoadSignBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class RoadSignBlock(properties: Properties) : HorizontalFacingBlockWithEntity(properties) {
    init {
        registerDefaultState(stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH))
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return RoadSignBlockEntity(pos, state)
    }

    override fun getCollisionShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return getShape(state, level, pos, context)
    }

    override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape {
        return Shapes.empty()
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        if (player.isShiftKeyDown) {
            return InteractionResult.PASS
        }

        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is RoadSignBlockEntity) {
            // RoadSignEditScreen.openScreen(blockEntity)
            player.sendSystemMessage(Component.literal("Hello"))
        }

        return InteractionResult.SUCCESS
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape {
        return when (state.getValue(HORIZONTAL_FACING)) {
            Direction.NORTH -> NORTH_SHAPE
            Direction.SOUTH -> SOUTH_SHAPE
            Direction.WEST -> WEST_SHAPE
            Direction.EAST -> EAST_SHAPE
            else -> Shapes.block()
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.horizontalDirection)
    }

    companion object {
        private val NORTH_SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 0.03)
        private val SOUTH_SHAPE: VoxelShape = NORTH_SHAPE.move(0.0, 0.0, 0.97)
        private val WEST_SHAPE: VoxelShape = Shapes.box(0.0, 0.0, 0.0, 0.03, 1.0, 1.0)
        private val EAST_SHAPE: VoxelShape = WEST_SHAPE.move(0.97, 0.0, 0.0)
    }
}
