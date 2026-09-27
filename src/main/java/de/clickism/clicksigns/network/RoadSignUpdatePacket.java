package de.clickism.clicksigns.network;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.network.Packet;
import de.clickism.clicksigns.platform.network.PacketType;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.codec.RoadSignCodec;
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
            try {
                buf.writeBlockPos(packet.pos());
                RoadSignCodec.codec().writePacket(buf, packet.roadSign());
            } catch (Exception e) {
                ClickSigns.LOGGER.error("Error writing RoadSignUpdatePacket", e);
            }
        },
        // Reader
        (buf) -> {
            try {
                BlockPos pos = buf.readBlockPos();
                RoadSign roadSign = RoadSignCodec.codec().readPacket(buf);
                return new RoadSignUpdatePacket(pos, roadSign);
            } catch (Exception e) {
                ClickSigns.LOGGER.error("Error reading RoadSignUpdatePacket", e);
                return null;
            }
        },
        // Server Handler
        (packet, player) -> {
            try {
                var level = player.serverLevel();
                var blockEntity = level.getBlockEntity(packet.pos());
                if (!(blockEntity instanceof RoadSignBlockEntity roadSignBlockEntity)) return;
                // Update road sign
                roadSignBlockEntity.updateRoadSign(packet.roadSign());
                Platform.network().sendToAllInLevel(level, packet);
            } catch (Exception e) {
                ClickSigns.LOGGER.error("Error handling RoadSignUpdatePacket", e);
            }
        },
        // Client Handler
        (packet) -> {
            try {
                var client = Minecraft.getInstance();
                var level = client.level;
                if (level == null) return;
                var blockEntity = level.getBlockEntity(packet.pos());
                if (!(blockEntity instanceof RoadSignBlockEntity roadSignBlockEntity)) return;
                // Update road sign
                roadSignBlockEntity.updateRoadSign(packet.roadSign());
            } catch (Exception e) {
                ClickSigns.LOGGER.error("Error handling RoadSignUpdatePacket on client", e);
            }
        }
    );

    @Override
    public PacketType<?> type() {
        return TYPE;
    }
}
