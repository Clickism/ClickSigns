package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlockEntityTypes;
import de.clickism.clicksigns.serialization.TagReader;
import de.clickism.clicksigns.serialization.TagWriter;
import de.clickism.clicksigns.sign.RoadSign;
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

//? if >=26.1 {
import de.clickism.clicksigns.serialization.ValueTagImpl;
import org.jspecify.annotations.NonNull;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.util.ProblemReporter;
//?} else
//import de.clickism.clicksigns.serialization.NbtTagImpl;

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
        //? if >=26.1 {
        var tag = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
        //?} else
        //var tag = new CompoundTag();
        this.saveAdditional(
            tag
            //? if >= 1.21.1 && <26.1
            //,provider
        );
        //? if >=26.1 {
        return tag.buildResult();
        //?} else
        //return tag;
    }

    //? if >=26.1 {

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        writeRoadSign(ValueTagImpl.writer(output));
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        this.roadSign = readRoadSign(ValueTagImpl.reader(input));
    }

    //?} else {

    /*@Override
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
        writeRoadSign(new NbtTagImpl(tag));
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
        this.roadSign = readRoadSign(new NbtTagImpl(tag));
    }

    //~}

    *///?}

    private void writeRoadSign(TagWriter writer) {
        if (this.roadSign == null) return;
        RoadSignCodec.codec().writeTag(writer, this.roadSign);
    }

    private RoadSign readRoadSign(TagReader reader) {
        try {
            if (!RoadSignCodec.hasSign(reader)) {
                // No road sign data found in the tag, return null
                return null;
            }
            return RoadSignCodec.codec().readTag(reader);
        } catch (Exception e) {
            ClickSigns.LOGGER.error("Failed to read road sign from block entity at {}", worldPosition, e);
            return null;
        }
    }
}
