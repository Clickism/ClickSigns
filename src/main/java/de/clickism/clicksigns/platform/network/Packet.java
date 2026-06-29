package de.clickism.clicksigns.platform.network;

//? if >= 1.21.1 {
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? }
/**
 * Packet interface representing a network packet.
 */
public interface Packet /*?if >= 1.21.1 {*/ extends CustomPacketPayload /*?} */  {
    /**
     * The packet type of this packet.
     * Implemented this way to support the new CustomPacketPayload API
     *
     * @return the packet type of this packet
     */
    PacketType<? extends Packet> subtype();
    //? if < 1.21.1 {
    /*PacketType<? extends Packet> type();
    *///? } elif >= 1.21.1 {
    Type<? extends Packet> type();
    //?}
}
