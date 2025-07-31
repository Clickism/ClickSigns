/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.entity;

import me.clickism.clicksigns.sign.RoadSign;
import me.clickism.clicksigns.util.nbt.NbtHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Optional;

//? if >=1.21.6 {
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
//?}

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
    //? if >=1.21.6 {
    protected void writeData(WriteView readView) {
    //?} elif >=1.20.5 {
    /*protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    *///?} else {
    /*public void writeNbt(NbtCompound nbt) {
    *///?}
        //? if >=1.21.6 {
        super.writeData(readView);
        //?} elif >=1.20.5 {
        /*super.writeNbt(nbt, registryLookup);
        *///?} else {
        /*super.writeNbt(nbt);
        *///?}
        //? if >=1.21.6 {
        NbtHelper helper = NbtHelper.create(readView, null);
         //?} else
        /*NbtHelper helper = NbtHelper.create(nbt);*/
        if (roadSign == null) return;
        helper.put("template", roadSign.templateId().toString());
        helper.putStringList("texts", roadSign.getTexts());
        helper.put("textureIndex", roadSign.getTextureIndex());
        helper.put("alignment", roadSign.getAlignment().ordinal());
    }



    @Override
    //? if >=1.21.6 {
    protected void readData(ReadView readView) {
    //?} elif >=1.20.5 {
    /*protected void readNbt(NbtCompound nbtCompound, RegistryWrapper.WrapperLookup registryLookup) {
    *///?} else {
    /*public void readNbt(NbtCompound nbtCompound) {
    *///?}
        //? if >=1.21.6 {
        super.readData(readView);
        //?} elif >=1.20.5 {
        /*super.readNbt(nbtCompound, registryLookup);
        *///?} else {
        /*super.readNbt(nbtCompound);
        *///?}

        //? if >=1.21.6 {
        NbtHelper helper = NbtHelper.create(null, readView);
        //?} else
        /*NbtHelper helper = NbtHelper.create(nbtCompound);*/
        String templateId = helper.getOrDefault("template", "");
        if (templateId.isEmpty()) return;
        Identifier id = Identifier.tryParse(templateId);
        List<String> texts = helper.getStringList("texts");
        int textureIndex = helper.getOrDefault("textureIndex", 0);
        int alignment = helper.getOrDefault("alignment", RoadSign.Alignment.CENTER.ordinal());
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
