package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlockEntityTypes;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.util.nbt.NbtReaderWriterImpl;
import net.minecraft.core.BlockPos;
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
    public @NotNull CompoundTag getUpdateTag() {
        var tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.roadSign == null) return;
        var writer = new NbtReaderWriterImpl(tag);
        RoadSign.NBT_WRITER.write(writer, this.roadSign);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        var reader = new NbtReaderWriterImpl(tag);
        try {
            this.roadSign = RoadSign.NBT_READER.read(reader);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to read road sign from block entity at {}", worldPosition, e);
        }
    }
}
