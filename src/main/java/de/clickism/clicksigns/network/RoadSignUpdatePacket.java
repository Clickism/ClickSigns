package de.clickism.clicksigns.network;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.network.Packet;
import de.clickism.clicksigns.platform.network.PacketType;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public record RoadSignUpdatePacket(
        BlockPos pos,
        RoadSign roadSign
) implements Packet {

    public static final PacketType<RoadSignUpdatePacket> TYPE = new PacketType<>(
            ClickSigns.identifier("road_sign_update"),
            // Writer
            (buf, packet) -> {
                buf.writeBlockPos(packet.pos());
                RoadSign.WRITER.accept(buf, packet.roadSign());
            },
            // Reader
            (buf) -> {
                BlockPos pos = buf.readBlockPos();
                RoadSign roadSign = RoadSign.READER.apply(buf);
                return new RoadSignUpdatePacket(pos, roadSign);
            },
            // Server Handler
            (packet, player) -> {
                player.sendSystemMessage(Component.literal("Received road sign:" + packet.toString()));
                Platform.network().sendToPlayer(player, packet);
            },
            // Client Handler
            (packet) -> {
                ClickSigns.LOGGER.info("Received road sign update packet: {}", packet);
            }
    );

    @Override
    public PacketType<?> type() {
        return TYPE;
    }
}
