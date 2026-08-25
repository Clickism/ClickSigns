package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlockEntityTypes;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.util.nbt.NbtReaderWriterImpl;
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
//? if >= 26.1 {
/*
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.TagValueInput;
*///?}

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

    //? if < 26.1 {
    @Override
    protected void saveAdditional(
            CompoundTag tag
            //? if >= 1.21.1
            , HolderLookup.Provider provider
    ) {
        super.saveAdditional(
                tag
                //? if >= 1.21.1
                , provider
        );
        if (this.roadSign == null) return;
        var writer = new NbtReaderWriterImpl(tag);
        RoadSign.NBT_WRITER.write(writer, this.roadSign);
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
                , provider
        );
        return tag;
    }
    //? } elif >= 26.1 {
    /*@Override
    public @NotNull CompoundTag getUpdateTag(
            //? if >= 1.21.1
            HolderLookup.Provider provider
    ) {
        var tag = new CompoundTag();
        this.saveAdditional(
                tag
                //? if >= 1.21.1
                , provider
        );
        return tag;
    }

    @Override
    protected void saveAdditional(
            final ValueOutput output
    ) {
        super.saveAdditional(
                output
        );
        if (this.roadSign == null) return;
        var writer = new NbtReaderWriterImpl(output);
        RoadSign.NBT_WRITER.write(writer, this.roadSign);
    }
    *///?}

    //? if < 1.21 {
    /*@Override
    public void load(CompoundTag tag) {
        super.load(tag);
        var reader = new NbtReaderWriterImpl(tag);
        try {
            this.roadSign = RoadSign.NBT_READER.read(reader);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to read road sign from block entity at {}", worldPosition, e);
        }
    }
    *///? }
    //? if >= 1.21.1 {
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        var reader = new NbtReaderWriterImpl(tag);
        try {
            this.roadSign = RoadSign.NBT_READER.read(reader);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to read road sign from block entity at {}", worldPosition, e);
        }
    }
    //? }
}
