package de.clickism.clicksigns.platform.neoforge;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import static net.neoforged.api.distmarker.Dist.CLIENT;

//? if >= 26.1 {
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
//?} else {
/*import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
*///?}

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
    public void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playBidirectional(
            GlobalPacket.TYPE,
            GlobalPacket.CODEC,
            //? if <26.1 {
            /*new DirectionalPayloadHandler<>(
                (payload, context) -> {
                    handleClient(payload.packet());
                },
                *///?}
                (payload, context) -> {
                    var packet = payload.packet();
                    var player = (ServerPlayer) context.player();
                    //? if >=26.1 {
                    var server = player.level().getServer();
                    //?} else
                    //var server = player.getServer();
                    if (server == null) {
                        throw new IllegalStateException("Server is null in server context whilst handling packet: " + packet.type().id());
                    }
                    handleServer(packet, server, player);
                }
            //? if <26.1
            //)
        );
    }

    @Override
    public void sendToServer(Packet packet) {
        //? if >=26.1 {
        ClientPacketDistributor.sendToServer(new GlobalPacket(packet));
        //?} else
        //PacketDistributor.sendToServer(new GlobalPacket(packet));
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

    //? if >=26.1 {
    @EventBusSubscriber(modid = ClickSigns.MOD_ID, value = CLIENT)
    private static class ClientEvents {
        @SubscribeEvent
        public static void register(RegisterClientPayloadHandlersEvent event) {
            event.register(
                GlobalPacket.TYPE,
                (payload, context) -> {
                    handleClient(payload.packet());
                }
            );
        }
    }
    //?}
}
