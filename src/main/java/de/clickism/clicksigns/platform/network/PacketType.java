package de.clickism.clicksigns.platform.network;

import net.minecraft.network.FriendlyByteBuf;
//? if >= 1.21.1 {
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Represents a type of packet, containing the logic to encode, decode and handle the packet.
 *
 * @param id            the unique id of the packet type
//? if < 1.21.1
//* @param writer        the function to write the packet data to a buffer
//? if < 1.21.1
//* @param reader        the function to read the packet data from a buffer
//? if >= 1.21.1
 * @param packet        the codec to write/read the packet data to/from a buffer
 * @param serverHandler the function to handle the packet on the server side
 * @param clientHandler the function to handle the packet on the client side
 * @param <T>           the type of payload of the packet
 */
public record PacketType<T extends Packet>(
        ResourceLocation id,
        //? if < 1.21.1 {
        /*FriendlyByteBuf.Writer<T> writer,
        FriendlyByteBuf.Reader<T> reader,
        *///? }
        //? if >= 1.21.1
        StreamCodec<FriendlyByteBuf, T> packet,
        ServerHandler<T> serverHandler,
        ClientHandler<T> clientHandler
) {
    /**
     * Interface for handling packets on the server side.
     *
     * @param <T> the type of the packet
     */
    public interface ServerHandler<T extends Packet> {
        void handle(T packet, ServerPlayer player);
    }

    /**
     * Interface for handling packets on the client side.
     *
     * @param <T> the type of the packet
     */
    public interface ClientHandler<T extends Packet> {
        void handle(T packet);
    }
}
