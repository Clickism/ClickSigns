package de.clickism.clicksigns.serialization;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

//? if >= 1.21.1
import net.minecraft.network.codec.StreamEncoder;

public interface PacketWriter<T>
    //? if >= 1.21.1 {
    extends StreamEncoder<FriendlyByteBuf, T>
    //?} else
    //extends FriendlyByteBuf.Writer<T>
{
    void write(FriendlyByteBuf buf, @NotNull T packet);

    //? if >= 1.21.1 {
    @Override
    default void encode(FriendlyByteBuf buf, @NotNull T packet) {
        write(buf, packet);
    }
    //?} else {
    /*@Override
    default void accept(FriendlyByteBuf buf, @NotNull T packet) {
        write(buf, packet);
    }
    *///?}
}
