package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetwork extends Network {
    public static final ResourceLocation CHANNEL = ClickSigns.identifier("main");

    public void init() {
        ServerPlayNetworking.registerGlobalReceiver(
                CHANNEL,
                (server, player, handler, buf, responseSender) -> {
                    onReceiveServer(buf, server, player);
                }
        );
        ClientPlayNetworking.registerGlobalReceiver(
                CHANNEL,
                (client, handler, buf, responseSender) -> {
                    onReceiveClient(buf);
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
}
