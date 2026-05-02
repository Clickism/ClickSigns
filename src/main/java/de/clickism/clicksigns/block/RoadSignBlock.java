package de.clickism.clicksigns.block;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.SignScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Road sign block
 */
@SuppressWarnings("deprecation")
public class RoadSignBlock extends HorizontalFacingBlockWithEntity {

    // Shapes for each facing direction
    private static final double THICKNESS = 0.03;
    private static final VoxelShape NORTH_SHAPE = Shapes.box(0, 0, 0, 1, 1, THICKNESS);
    private static final VoxelShape SOUTH_SHAPE = NORTH_SHAPE.move(0, 0, 1 - THICKNESS);
    private static final VoxelShape WEST_SHAPE = Shapes.box(0, 0, 0, THICKNESS, 1, 1);
    private static final VoxelShape EAST_SHAPE = WEST_SHAPE.move(1 - THICKNESS, 0, 0);

    public RoadSignBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoadSignBlockEntity(pos, state);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        return getShape(state, level, pos, context);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(
            @NotNull BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos
    ) {
        return Shapes.empty();
    }

    @Override
    public @NotNull InteractionResult use(
            @NotNull BlockState state,
            @NotNull Level level,
            @NotNull BlockPos pos,
            @NotNull Player player,
            @NotNull InteractionHand hand,
            @NotNull BlockHitResult hit
    ) {
        if (!level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof RoadSignBlockEntity roadSignEntity) {
            GuiUtils.openScreen(new SignScreen(null, roadSignEntity));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @NotNull VoxelShape getShape(
            BlockState state,
            @NotNull BlockGetter level,
            @NotNull BlockPos pos,
            @NotNull CollisionContext context
    ) {
        // Return shape based on facing direction
        var facing = state.getValue(HORIZONTAL_FACING);
        return switch (facing) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> Shapes.block();
        };
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }
}