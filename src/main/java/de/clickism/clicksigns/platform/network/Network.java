package de.clickism.clicksigns.platform.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class Network {
    public abstract void sendToServer(Packet packet);

    public abstract void sendToPlayer(ServerPlayer player, Packet packet);

    protected void onReceiveServer(
            FriendlyByteBuf buf,
            MinecraftServer server,
            ServerPlayer player
    ) {
        var packet = readPacket(buf);
        handleServer(packet, server, player);
    }

    protected void onReceiveClient(FriendlyByteBuf buf) {
        var packet = readPacket(buf);
        handleClient(packet);
    }

    @SuppressWarnings("unchecked")
    private <T extends Packet> void handleServer(T packet, MinecraftServer server, ServerPlayer player) {
        var type = (PacketType<T>) packet.type();
        server.execute(() -> type.serverHandler().handle(packet, player));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Packet> void handleClient(T packet) {
        PacketType<T> type = (PacketType<T>) packet.type();
        type.clientHandler().handle(packet);
    }

    // Read and write packets

    @SuppressWarnings("unchecked")
    protected static <T extends Packet> FriendlyByteBuf writePacket(T packet) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        var type = (PacketType<T>) packet.type();
        buf.writeResourceLocation(type.id());
        type.writer().accept(buf, packet);
        return buf;
    }

    public static Packet readPacket(FriendlyByteBuf buf) {
        var id = buf.readResourceLocation();
        var type = PacketRegistry.get(id);
        if (type == null) {
            throw new IllegalStateException("Received packet with unknown id: " + id);
        }
        return type.reader().apply(buf);
    }
}
