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
    // TODO: Remove testing stuff
    static {
        TileSetRegistry.register(new TileSet(ClickSigns.identifier("roadsigns/tileset/white.png"), 4, 8));
        TileSetRegistry.register(new TileSet(ClickSigns.identifier("roadsigns/tileset/back.png"), 4, 8));
    }

    private RoadSign roadSign = new RoadSign(
            TiledTextureGenerator.generateAndRegister(
                    new TileSet(ClickSigns.identifier("roadsigns/tileset/white.png"), 4, 8),
                    2f, 1f
            ),
            TiledTextureGenerator.generateAndRegister(
                    new TileSet(ClickSigns.identifier("roadsigns/tileset/back.png"), 4, 8),
                    2f, 1f
            ),
            List.of(
                    new SymbolElement(2, 2, Alignment.TOP_RIGHT, Texture.load(ClickSigns.identifier("roadsigns/symbols/arrows/right.png"))),
                    new TextElement(16, 8, Alignment.TOP_CENTER, "Main Street", 1f, SignColors.WHITE.getRGB(), SignColors.BLUE.getRGB())
            )
    );

    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROAD_SIGN.get(), pos, state);
    }

    public RoadSign roadSign() {
        return roadSign;
    }

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
}
