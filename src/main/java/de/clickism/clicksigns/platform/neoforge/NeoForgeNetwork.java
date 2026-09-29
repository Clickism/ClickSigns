package de.clickism.clicksigns.platform.neoforge;

import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Forge implementation of the network system.
 */
public class NeoForgeNetwork extends Network {
    /**
     * The fabric network instance
     */
    public static final NeoForgeNetwork INSTANCE = new NeoForgeNetwork();

    private NeoForgeNetwork() {
        // Singleton class
    }

    public void initialize(IEventBus modBus) {
        modBus.register(this);
    }

    @Override
    public void registerServer() {
        // Handled independently because neoforge is special
    }

    @Override
    public void registerClient() {
        // No need to register anything for the client side in Neoforge
    }

    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playBidirectional(
            GlobalPacket.TYPE,
            GlobalPacket.CODEC,
            new DirectionalPayloadHandler<>(
                (payload, context) -> {
                    handleClient(payload.packet());
                },
                (payload, context) -> {
                    var packet = payload.packet();
                    var player = (ServerPlayer) context.player();
                    var server = player.getServer();
                    if (server == null) {
                        throw new IllegalStateException("Server is null in server context whilst handling packet: " + packet.type().id());
                    }
                    handleServer(packet, server, player);
                }
            )
        );
    }

    @Override
    public void sendToServer(Packet packet) {
        PacketDistributor.sendToServer(new GlobalPacket(packet));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Packet packet) {
        PacketDistributor.sendToPlayer(player, new GlobalPacket(packet));
    }

    @Override
    public void sendToAllInLevel(ServerLevel level, Packet packet) {
        level.getServer().execute(() -> {
            level.players().forEach(player -> sendToPlayer(player, packet));
        });
    }
}
