package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.SignColors;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.util.texture.Texture;
import de.clickism.clicksigns.util.texture.TileSet;
import de.clickism.clicksigns.util.texture.TiledTextureGenerator;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

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
        super(ModBlockEntityTypes.ROAD_SIGN.get(), pos, state);
    }

    // TODO: Save entity data

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

    // Just use the byte encoders to save data, because why not

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (this.roadSign == null) return;
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        RoadSign.WRITER.accept(buf, this.roadSign);
        var bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        tag.putByteArray("roadSignBytes", bytes);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (!tag.contains("roadSignBytes")) return;
        var bytes = tag.getByteArray("roadSignBytes");
        var buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
        this.roadSign = RoadSign.READER.apply(buf);
    }
}
