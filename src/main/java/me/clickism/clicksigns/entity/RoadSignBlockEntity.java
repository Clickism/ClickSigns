/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.entity;

import com.mojang.serialization.Codec;
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

import static me.clickism.clicksigns.ClickSigns.unwrap;

//? if >=1.21.6 {
/*import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
*///?}

public class RoadSignBlockEntity extends BlockEntity {
    private static final String TEMPLATE_KEY = "template";
    private static final String TEXTS_KEY = "texts";
    private static final String TEXTURE_INDEX_KEY = "textureIndex";
    private static final String ALIGNMENT_KEY = "alignment";

    private RoadSign roadSign = null;

    public RoadSignBlockEntity(BlockPos pos, BlockState state) {
        super(unwrap(ModBlockEntityTypes.ROAD_SIGN), pos, state);
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
    /*protected void writeData(WriteView nbt) {
    *///?} elif >=1.20.5 {
    /*protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    *///?} else {
    public void writeNbt(NbtCompound nbt) {
    //?}
        //? if >=1.21.6 {
        /*super.writeData(nbt);
        *///?} elif >=1.20.5 {
        /*super.writeNbt(nbt, registryLookup);
        *///?} else {
        super.writeNbt(nbt);
        //?}
        if (roadSign == null) return;
        //? if <1.21.6 {
        NbtList list = new NbtList();
        List<NbtString> roadSignTexts = roadSign.getTexts()
                .stream()
                .map(NbtString::of)
                .toList();
        list.addAll(roadSignTexts);
        //?}
        // Write Nbt
        nbt.putString(TEMPLATE_KEY, roadSign.templateId().toString());
        nbt.putInt(TEXTURE_INDEX_KEY, roadSign.getTextureIndex());
        nbt.putInt(ALIGNMENT_KEY, roadSign.getAlignment().ordinal());
        // Write List
        //? if >=1.21.6 {
        /*nbt.remove(TEXTS_KEY);
        var appender = nbt.getListAppender(TEXTS_KEY, Codec.STRING);
        roadSign.getTexts().forEach(appender::add);
        *///?} else
        nbt.put(TEXTS_KEY, list);
    }

    @Override
    //? if >=1.21.6 {
    /*protected void readData(ReadView readView) {
    *///?} elif >=1.20.5 {
    /*protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
    *///?} else {
    public void readNbt(NbtCompound nbt) {
     //?}
        //? if >=1.21.6 {
        /*super.readData(readView);
         *///?} elif >=1.20.5 {
        /*super.readNbt(nbt, registryLookup);
        *///?} else {
        super.readNbt(nbt);
         //?}
        int defaultAlignment = RoadSign.Alignment.CENTER.ordinal();

        // Read Nbt
        //? if >=1.21.6 {
        /*String templateId = readView.getString(TEMPLATE_KEY, "");
        List<String> texts = readView.getTypedListView(TEXTS_KEY, Codec.STRING)
                .stream()
                .toList();
        int textureIndex = readView.getInt(TEXTURE_INDEX_KEY, 0);
        int alignment = readView.getInt(ALIGNMENT_KEY, defaultAlignment);
        *///?} elif >=1.21.5 {
        /*String templateId = nbt.getString(TEMPLATE_KEY).orElse("");
        List<String> texts = getTextsFromNbt(nbt.getListOrEmpty(TEXTS_KEY));
        int textureIndex = nbt.getInt(TEXTURE_INDEX_KEY).orElse(0);
        int alignment = nbt.getInt(ALIGNMENT_KEY).orElse(defaultAlignment);
        *///?} else {
        String templateId = nbt.getString(TEMPLATE_KEY);
        List<String> texts = getTextsFromNbt(nbt.getList(TEXTS_KEY, NbtElement.STRING_TYPE));
        int textureIndex = nbt.getInt(TEXTURE_INDEX_KEY);
        int alignment = nbt.getInt(ALIGNMENT_KEY);
        //?}

        // Parse
        if (templateId.isEmpty()) return;
        Identifier id = Identifier.tryParse(templateId);
        this.roadSign = new RoadSign(id, textureIndex, texts, RoadSign.Alignment.values()[alignment]);
    }

    private List<String> getTextsFromNbt(NbtList nbtList) {
        return nbtList.stream()
                .map(NbtElement::asString)
                //? if >=1.21.5
                /*.map(Optional::orElseThrow)*/
                .toList();
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    //? if >=1.20.5 {
    /*@Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createComponentlessNbt(registryLookup);
    }
    *///?} else {
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        this.writeNbt(nbtCompound);
        return nbtCompound;
    }
    //?}
}
