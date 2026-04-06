package de.clickism.clicksigns.entity;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import de.clickism.clicksigns.util.texture.TileSet;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.util.texture.StaticTexture;
import de.clickism.clicksigns.util.texture.TiledTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
    private final RoadSign roadSign = new RoadSign(
            TiledTexture.fromTileSet(
                    TileSet.load(ClickSigns.identifier("roadsigns/tileset/white.png"), 4, 8),
                    2f, 1f
            ),
            TiledTexture.fromTileSet(
                    TileSet.load(ClickSigns.identifier("roadsigns/tileset/back.png"), 4, 8),
                    2f, 1f
            ),
            List.of(
                    new SymbolElement(2, 2, StaticTexture.load(ClickSigns.identifier("roadsigns/symbols/arrows/right.png"))),
                    new TextElement(16, 8, "Main Street", Color.BLACK, 1f)
            )
    );

    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROAD_SIGN.get(), pos, state);
    }

    public RoadSign roadSign() {
        return roadSign;
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
}
