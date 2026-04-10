package de.clickism.clicksigns.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Represents a type of packet, containing the logic to encode, decode and handle the packet.
 *
 * @param id            the unique id of the packet type
 * @param writer        the function to write the packet data to a buffer
 * @param reader        the function to read the packet data from a buffer
 * @param serverHandler the function to handle the packet on the server side
 * @param clientHandler the function to handle the packet on the client side
 * @param <T>           the type of payload of the packet
 */
public record PacketType<T extends Packet>(
        ResourceLocation id,
        FriendlyByteBuf.Writer<T> writer,
        FriendlyByteBuf.Reader<T> reader,
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
