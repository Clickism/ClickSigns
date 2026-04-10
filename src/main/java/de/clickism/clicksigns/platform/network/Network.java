package de.clickism.clicksigns.platform.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Network system abstraction.
 */
public abstract class Network {
    /**
     * Sends the given packet to the server.
     *
     * @param packet The packet to be sent
     */
    public abstract void sendToServer(Packet packet);

    /**
     * Sends the given packet to the given player.
     *
     * @param player The player to send the packet to
     * @param packet The packet to be sent
     */
    public abstract void sendToPlayer(ServerPlayer player, Packet packet);

    /**
     * Sends the given packet to all players.
     *
     * @param packet The packet to be sent
     */
    public abstract void sendToAllInLevel(ServerLevel level, Packet packet);

    /**
     * Registers the network handlers.
     * Should be called during mod initialization.
     */
    public abstract void register();

    /**
     * Handles a packet received on the server side.
     *
     * @param packet the packet to be handled
     * @param server the server instance
     * @param player the player who sent the packet
     * @param <T>    the type of the packet
     */
    @SuppressWarnings("unchecked")
    protected <T extends Packet> void handleServer(T packet, MinecraftServer server, ServerPlayer player) {
        var type = (PacketType<T>) packet.type();
        server.execute(() -> type.serverHandler().handle(packet, player));
    }

    /**
     * Handles a packet received on the client side.
     *
     * @param packet the packet to be handled
     * @param <T>    the type of the packet
     */
    @SuppressWarnings("unchecked")
    protected static <T extends Packet> void handleClient(T packet) {
        PacketType<T> type = (PacketType<T>) packet.type();
        type.clientHandler().handle(packet);
    }

    /**
     * Writes a packet to a new buffer, with the packet type id as header.
     *
     * @param packet the packet to be written
     * @param <T>    the type of the packet
     * @return buffer containing the packet data
     */
    @SuppressWarnings("unchecked")
    protected static <T extends Packet> FriendlyByteBuf writePacket(T packet) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        // Write packet id
        var type = (PacketType<T>) packet.type();
        buf.writeResourceLocation(type.id());
        // Write packet data
        type.writer().accept(buf, packet);
        return buf;
    }

    /**
     * Reads a packet from the given buffer, retrieving the packet type id from the header.
     *
     * @param buf buffer to read from
     * @return the read packet
     */
    protected static Packet readPacket(FriendlyByteBuf buf) {
        // Read packet id
        var id = buf.readResourceLocation();
        var type = PacketRegistry.get(id);
        if (type == null) {
            throw new IllegalStateException("Received packet with unknown id: " + id);
        }
        // Read packet data
        return type.reader().apply(buf);
    }
}
