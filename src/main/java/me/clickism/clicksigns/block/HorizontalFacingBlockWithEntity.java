/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.block;

//? if >=1.21.2 {
/*import net.minecraft.state.property.EnumProperty;
*///?} else {
import net.minecraft.state.property.DirectionProperty;
//?}

import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;

public abstract class HorizontalFacingBlockWithEntity extends BlockWithEntity {
    //? if >=1.21.2 {
    /*public static final EnumProperty<Direction>FACING = Properties.HORIZONTAL_FACING;
    *///?} else {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    //?}

    protected HorizontalFacingBlockWithEntity(Settings settings) {
        super(settings);
    }

    //? if >=1.20.5 {
    /*@Override
    protected abstract MapCodec<? extends BlockWithEntity> getCodec();
    *///?}

    @Override
    //? if >=1.20.5 {
    /*protected
     *///?} else {
    public
    //?}
    BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    //? if >=1.20.5 {
    /*protected
     *///?} else {
    public
    //?}
    BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }
}
