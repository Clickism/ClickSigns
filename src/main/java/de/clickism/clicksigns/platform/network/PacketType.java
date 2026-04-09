package de.clickism.clicksigns.platform.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record PacketType<T extends Packet>(
        ResourceLocation id,
        FriendlyByteBuf.Writer<T> writer,
        FriendlyByteBuf.Reader<T> reader,
        ServerHandler<T> serverHandler,
        ClientHandler<T> clientHandler
) {
    @FunctionalInterface
    public interface ServerHandler<T extends Packet> {
        void handle(T packet, ServerPlayer player);
    }

    @FunctionalInterface
    public interface ClientHandler<T extends Packet> {
        void handle(T packet);
    }
}
