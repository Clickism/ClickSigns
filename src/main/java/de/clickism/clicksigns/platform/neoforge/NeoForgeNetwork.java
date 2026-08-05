package de.clickism.clicksigns.platform.neoforge;

import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
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

    @Override public void register() {
        
    }

    public void register(PayloadRegistrar registrar) {
        registrar.playBidirectional(
                RoadSignUpdatePacket.TYPE,
                RoadSignUpdatePacket.SUBTYPE.packet(),
                new DirectionalPayloadHandler<>(
                        (packet, context) -> {
                            handleClient(packet);
                        },
                        (packet, context) -> {
                            handleServer(
                                    packet,
                                    context.player().getServer(),
                                    context.player().getServer().getPlayerList().getPlayer(context.player().getUUID())
                            );
                        }
                )
        );
    }

    @Override
    public void sendToServer(Packet packet) {
        PacketDistributor.sendToServer(packet);
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Packet packet) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    @Override
    public void sendToAllInLevel(ServerLevel level, Packet packet) {
        level.getServer().execute(() -> {
            level.players().forEach(player -> sendToPlayer(player, packet));
        });
    }

    /**
     * Wrapper for packets to be sent through the Forge networking API.
     * For some reason, subclasses give an error, so we need to use a wrapper.
     *
     * @param packet The packet to be sent
     */
    private record ForgePacket(Packet packet) {}
}
