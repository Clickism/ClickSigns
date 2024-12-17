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
        // nbt.putBoolean("flipped", roadSign.isFlipped());
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
        String templateId = nbt.getString("template");
        if (templateId.isEmpty()) return;
        Identifier id = Identifier.tryParse(templateId);
        NbtList textsNbtList = nbt.getList("texts", NbtElement.STRING_TYPE);
        List<String> texts = List.of();
        if (textsNbtList != null) {
            texts = textsNbtList.stream()
                    .map(NbtElement::asString)
                    .toList();
        }
        int textureIndex = nbt.getInt("textureIndex");
        // boolean flipped = nbt.getBoolean("flipped");
        int alignment = nbt.getInt("alignment");
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
    //?}
}
