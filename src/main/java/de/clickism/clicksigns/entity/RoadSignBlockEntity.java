package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Road sign block entity
 */
public class RoadSignBlockEntity extends BlockEntity {
    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROAD_SIGN.get(), pos, state);
    }
}
