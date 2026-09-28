package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlockEntityTypes;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.serialization.NbtTagImpl;
import de.clickism.clicksigns.sign.codec.RoadSignCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Road sign block entity
 */
public class RoadSignBlockEntity extends BlockEntity {
    @Nullable
    private RoadSign roadSign;

    /**
     * Creates a new road sign block entity.
     *
     * @param pos   the position of the block entity
     * @param state the block state of the block entity
     */
    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ClickSignsBlockEntityTypes.ROAD_SIGN.get(), pos, state);
    }

    /**
     * Gets the road sign of this block entity.
     *
     * @return the road sign of this block entity, or null if none is set
     */
    public @Nullable RoadSign roadSign() {
        return roadSign;
    }

    /**
     * Updates the road sign of this block entity.
     *
     * @param roadSign new road sign to set
     */
    public void updateRoadSign(RoadSign roadSign) {
        this.roadSign = roadSign;
        setChanged();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level == null) return;
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 0);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(
        //? if >= 1.21.1
        HolderLookup.Provider provider
    ) {
        var tag = new CompoundTag();
        this.saveAdditional(
            tag
            //? if >= 1.21.1
            ,provider
        );
        return tag;
    }

    @Override
    protected void saveAdditional(
        CompoundTag tag
        //? if >= 1.21.1
        ,HolderLookup.Provider provider
    ) {
        super.saveAdditional(
            tag
            //? if >= 1.21.1
            ,provider
        );
        if (this.roadSign == null) return;
        var writer = new NbtTagImpl(tag);
        RoadSignCodec.codec().writeTag(writer, this.roadSign);
    }

    //~ if >= 1.21.1 'load' -> 'loadAdditional' {

    @Override
    public void loadAdditional(
        CompoundTag tag
        //? if >= 1.21.1
        ,HolderLookup.Provider provider
    ) {
        super.loadAdditional(
            tag
            //? if >= 1.21.1
            ,provider
        );
        var reader = new NbtTagImpl(tag);
        try {
            this.roadSign = RoadSignCodec.codec().readTag(reader);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to read road sign from block entity at {}", worldPosition, e);
        }
    }

    //~}
}
