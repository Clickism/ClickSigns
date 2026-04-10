package de.clickism.clicksigns.network;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.network.Packet;
import de.clickism.clicksigns.platform.network.PacketType;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

/**
 * Packet for updating a road sign
 *
 * @param pos      position of the road sign block entity
 * @param roadSign new road sign data
 */
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
                var level = player.serverLevel();
                var blockEntity = level.getBlockEntity(packet.pos());
                if (!(blockEntity instanceof RoadSignBlockEntity roadSignBlockEntity)) return;
                // Update road sign
                roadSignBlockEntity.updateRoadSign(packet.roadSign());
                Platform.network().sendToAllInLevel(level, packet);
            },
            // Client Handler
            (packet) -> {
                var client = Minecraft.getInstance();
                var level = client.level;
                if (level == null) return;
                var blockEntity = level.getBlockEntity(packet.pos());
                if (!(blockEntity instanceof RoadSignBlockEntity roadSignBlockEntity)) return;
                // Update road sign
                roadSignBlockEntity.updateRoadSign(packet.roadSign());
            }
    );

    @Override
    public PacketType<?> type() {
        return TYPE;
    }
}
