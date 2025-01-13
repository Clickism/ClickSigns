package me.clickism.clicksigns.block;

//? if >=1.21.2 {
import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
//?} elif >=1.20.5 {
/*import com.mojang.serialization.MapCodec;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemActionResult;
*///?}
import net.minecraft.block.*;
import me.clickism.clicksigns.entity.RoadSignBlockEntity;
import me.clickism.clicksigns.gui.RoadSignEditScreen;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RoadSignBlock extends HorizontalFacingBlockWithEntity implements BlockEntityProvider {
    //? if >=1.20.5
    public static final MapCodec<RoadSignBlock> CODEC = Block.createCodec(RoadSignBlock::new);

    public RoadSignBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    //? if >=1.20.5 {
    protected
    //?} else {
    /*public
    *///?}
    VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }


    @Override
    //? if >=1.20.5 {
    protected
    //?} else {
    /*public
    *///?}
    VoxelShape getCullingShape(BlockState state
            //? if <1.21.2
            /*, BlockView world, BlockPos pos*/
    ) {
        return VoxelShapes.empty();
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RoadSignBlockEntity(pos, state);
    }
    
    //? if >1.21.2 {
    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient) {
            return ActionResult.CONSUME;
        }
        onUse(state, world, pos, player, hit);
        return ActionResult.CONSUME;
    }
    //?} elif >=1.20.5 {
    /*@Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.isSneaking()) {
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!world.isClient) {
            return ItemActionResult.CONSUME;
        }
        onUse(state, world, pos, player, hit);
        return ItemActionResult.CONSUME;
    }
    *///?}

    @Override
    //? if >=1.20.5 {
    protected
     //?} else {
    /*public
    *///?}
    ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                 //? if <1.20.5
                                 /*Hand hand,*/
                                 BlockHitResult hit) {
        if (!world.isClient) {
            return ActionResult.PASS;
        }
        //? if <1.20.5 {
        /*if (player.isSneaking()) {
            return ActionResult.PASS;
        }
        *///?}
        if (world.getBlockEntity(pos) instanceof RoadSignBlockEntity entity) {
            RoadSignEditScreen.openScreen(entity);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    VoxelShape NORTH_SHAPE = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.03f);
    VoxelShape SOUTH_SHAPE = NORTH_SHAPE.offset(0f, 0f, 0.97f);
    VoxelShape WEST_SHAPE = VoxelShapes.cuboid(0f, 0f, 0f, 0.03f, 1f, 1f);
    VoxelShape EAST_SHAPE = WEST_SHAPE.offset(0.97f, 0f, 0f);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        Direction dir = state.get(FACING);
        VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.03f); // neg z
        VoxelShapes.cuboid(0f, 0f, 0f, 0.03f, 1f, 1f); // neg x

        return switch (dir) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> VoxelShapes.fullCube();
        };
    }

    @Override
    //? if >=1.20.5 {
    protected
     //?} else {
    /*public
    *///?}
    BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing());
    }

    //? if >=1.20.5 {
    @Override
    protected MapCodec<? extends RoadSignBlock> getCodec() {
        return CODEC;
    }
    //?}
}
