package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Fabric implementation of the network system.
 */
public class FabricNetwork extends Network {
    /**
     * The fabric network instance
     */
    public static final FabricNetwork INSTANCE = new FabricNetwork();

    private static final ResourceLocation CHANNEL = ClickSigns.identifier("main");

    private FabricNetwork() {
        // Singleton class
    }

    @Override
    public void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                CHANNEL,
                (server, player, handler, buf, responseSender) -> {
                    handleServer(readPacket(buf), server, player);
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                CHANNEL,
                (client, handler, buf, responseSender) -> {
                    handleClient(readPacket(buf));
                }
        );
    }

    @Override
    public void sendToServer(Packet packet) {
        ClientPlayNetworking.send(CHANNEL, writePacket(packet));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Packet packet) {
        ServerPlayNetworking.send(player, CHANNEL, writePacket(packet));
    }

    @Override
    public void sendToAllInLevel(ServerLevel level, Packet packet) {
        level.getServer().execute(() -> {
            PlayerLookup.world(level).forEach(player -> sendToPlayer(player, packet));
        });
    }
}
