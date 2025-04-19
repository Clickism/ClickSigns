/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.entity;

import me.clickism.clicksigns.sign.RoadSign;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

public class RoadSignBlockEntity extends BlockEntity {
    private RoadSign roadSign = null;

    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.ROAD_SIGN, pos, state);
    }

    @Nullable
    public RoadSign getRoadSign() {
        return roadSign;
    }

    public void setRoadSign(RoadSign roadSign) {
        this.roadSign = roadSign;
        this.markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), 0);
    }

    @Override
    //? if >=1.20.5 {
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    //?} else {
    /*public void writeNbt(NbtCompound nbt) {
    *///?}
        //? if >=1.20.5 {
        super.writeNbt(nbt, registryLookup);
        //?} else {
        /*super.writeNbt(nbt);
        *///?}
        if (roadSign == null) return;
        nbt.putString("template", roadSign.templateId().toString());
        NbtList list = new NbtList();
        list.addAll(roadSign.getTexts().stream().map(NbtString::of).toList());
        nbt.put("texts", list);
        nbt.putInt("textureIndex", roadSign.getTextureIndex());
        nbt.putInt("alignment", roadSign.getAlignment().ordinal());
    }



    @Override
    //? if >=1.20.5 {
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    //?} else {
    /*public void readNbt(NbtCompound nbt) {
    *///?}
        //? if >=1.20.5 {
        super.readNbt(nbt, registryLookup);
        //?} else {
        /*super.readNbt(nbt);
        *///?}

        String templateId = nbt.getString("template")
                //? if >=1.21.5
                .orElse("")
                ;
        if (templateId.isEmpty()) return;
        Identifier id = Identifier.tryParse(templateId);
        NbtList textsNbtList = nbt.getList("texts"
                //? if <1.21.5
                /*, NbtElement.STRING_TYPE*/
        )
                //? if >=1.21.5
                .orElse(null)
                ;
        List<String> texts = List.of();
        if (textsNbtList != null) {
            texts = textsNbtList.stream()
                    .map(NbtElement::asString)
                    //? if >=1.21.5
                    .map(Optional::orElseThrow)
                    .toList();
        }
        int textureIndex = nbt.getInt("textureIndex")
                //? if >=1.21.5
                .orElse(0)
                ;
        int alignment = nbt.getInt("alignment")
                //? if >=1.21.5
                .orElse(RoadSign.Alignment.CENTER.ordinal())
                ;
        this.roadSign = new RoadSign(id, textureIndex, texts, RoadSign.Alignment.values()[alignment]);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    //? if >=1.20.5 {
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createComponentlessNbt(registryLookup);
    }
    //?} else {
    /*@Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.writeNbt(nbtCompound);
        return nbtCompound;
    }
    *///?}
}
