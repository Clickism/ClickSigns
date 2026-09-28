package de.clickism.clicksigns.serialization;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

//? if >= 1.21.1
//import net.minecraft.network.codec.StreamDecoder;

public interface PacketReader<T>
    //? if >= 1.21.1 {
    /*extends StreamDecoder<FriendlyByteBuf, T>
    *///?} else
    extends FriendlyByteBuf.Reader<T>
{
    @NotNull T read(FriendlyByteBuf buf);

    //? if >= 1.21.1 {
    /*@Override
    default @NotNull T decode(FriendlyByteBuf buf) {
        return read(buf);
    }
    *///?} else {
    @Override
    default T apply(FriendlyByteBuf friendlyByteBuf) {
        return read(friendlyByteBuf);
    }
    //?}
}
