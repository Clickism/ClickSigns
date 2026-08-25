package de.clickism.clicksigns.network;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.network.Packet;
import de.clickism.clicksigns.platform.network.PacketType;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
//? if >= 1.21.1 {
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.codec.StreamMemberEncoder;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//? }

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
    public static final PacketType<RoadSignUpdatePacket> SUBTYPE = new PacketType<>(
            ClickSigns.identifier("road_sign_update"),
            //? if < 1.21.1 {
            /*// Writer
            (buf, packet) -> {
                buf.writeBlockPos(packet.pos());
                RoadSign.PACKET_WRITER.accept(buf, packet.roadSign());
            },
            // Reader
            (buf) -> {
                BlockPos pos = buf.readBlockPos();
                RoadSign roadSign = RoadSign.PACKET_READER.apply(buf);
                return new RoadSignUpdatePacket(pos, roadSign);
            },*///? }
            //? if >= 1.21.1 {
            StreamCodec.of(
                    (buf, packet) -> {
                        buf.writeBlockPos(packet.pos());
                        RoadSign.PACKET.encode(buf, packet.roadSign());
                    },
                    (buf) -> {
                        BlockPos pos = buf.readBlockPos();
                        RoadSign roadSign = RoadSign.PACKET.decode(buf);
                        return new RoadSignUpdatePacket(pos, roadSign);
                    }
            ),
            //? }
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
    public PacketType<?> subtype() {
        return SUBTYPE;
    }
    //? if < 1.21.1 {
    /*public static final PacketType<RoadSignUpdatePacket> TYPE = SUBTYPE;
    @Override
    public PacketType<?> type() {
        return TYPE;
    }
    *///? } elif >= 1.21.1 {
    public static final Type<RoadSignUpdatePacket> TYPE = new CustomPacketPayload.Type<RoadSignUpdatePacket>(SUBTYPE.id());
    public Type<? extends Packet> type() {
        return TYPE;
    };
    //? }
}
