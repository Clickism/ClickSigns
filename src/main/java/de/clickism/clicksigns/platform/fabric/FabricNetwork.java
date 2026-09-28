package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

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
    public void registerServer() {
        //? if >=1.21.1 {
        PayloadTypeRegistry.playC2S().register(GlobalPacket.TYPE, GlobalPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(GlobalPacket.TYPE, GlobalPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(
            GlobalPacket.TYPE,
            (payload, context) -> {
                var packet = payload.packet();
                handleServer(packet, context.server(), context.player());
            }
        );
        //?} else {
        /*ServerPlayNetworking.registerGlobalReceiver(
            CHANNEL,
            (server, player, handler, buf, responseSender) -> {
                handleServer(readPacket(buf), server, player);
            }
        );
        *///?}
    }

    @Override
    public void registerClient() {
        //? if >=1.21.1 {
        ClientPlayNetworking.registerGlobalReceiver(
            GlobalPacket.TYPE,
            (payload, context) -> {
                var packet = payload.packet();
                handleClient(packet);
            }
        );
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(
            CHANNEL,
            (client, handler, buf, responseSender) -> {
                handleClient(readPacket(buf));
            }
        );
        *///?}
    }

    @Override
    public void sendToServer(Packet packet) {
        //? if >= 1.21.1 {
        ClientPlayNetworking.send(new GlobalPacket(packet));
        //?} else
        //ClientPlayNetworking.send(CHANNEL, writePacket(packet));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Packet packet) {
        //? if >= 1.21.1 {
        ServerPlayNetworking.send(player, new GlobalPacket(packet));
        //?} else
        //ServerPlayNetworking.send(player, CHANNEL, writePacket(packet));
    }

    @Override
    public void sendToAllInLevel(ServerLevel level, Packet packet) {
        level.getServer().execute(() -> {
            PlayerLookup.world(level).forEach(player -> sendToPlayer(player, packet));
        });
    }

    //? if >= 1.21.1 {
    public record GlobalPacket(Packet packet) implements CustomPacketPayload {
        public static final Type<GlobalPacket> TYPE =
            new Type<>(ClickSigns.identifier("global_packet"));

        private static final StreamCodec<RegistryFriendlyByteBuf, GlobalPacket> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBytes(writePacket(packet.packet()));
            },
            buf -> {
                var packet = readPacket(buf);
                return new GlobalPacket(packet);
            }
        );

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    //?}
}
