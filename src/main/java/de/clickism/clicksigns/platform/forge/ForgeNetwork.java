package de.clickism.clicksigns.platform.forge;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.network.Network;
import de.clickism.clicksigns.platform.network.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Forge implementation of the network system.
 */
public class ForgeNetwork extends Network {
    /**
     * The fabric network instance
     */
    public static final ForgeNetwork INSTANCE = new ForgeNetwork();

    private static final int PROTOCOL_VERSION = 1;
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ClickSigns.identifier("main"),
            () -> String.valueOf(PROTOCOL_VERSION),
            version -> true,
            version -> true
    );

    private ForgeNetwork() {
        // Singleton class
    }

    @Override
    public void register() {
        CHANNEL.registerMessage(0, ForgePacket.class,
                // Encoder
                (packet, buf) -> buf.writeBytes(writePacket(packet.packet())),
                // Decoder
                (buf) -> new ForgePacket(readPacket(buf)),
                // Handler
                (packet, ctx) -> {
                    var context = ctx.get();
                    var player = context.getSender();
                    context.enqueueWork(() -> {
                        if (player != null) {
                            handleServer(packet.packet(), player.server, player);
                        } else {
                            handleClient(packet.packet());
                        }
                    });
                    context.setPacketHandled(true);
                });
    }

    @Override
    public void sendToServer(Packet packet) {
        CHANNEL.sendToServer(new ForgePacket(packet));
    }

    @Override
    public void sendToPlayer(ServerPlayer player, Packet packet) {
        CHANNEL.sendTo(new ForgePacket(packet), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
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
