package de.clickism.clicksigns.network;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.network.Packet;
import de.clickism.clicksigns.platform.network.PacketType;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.Pack;

import java.util.List;

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

            },
            // Client Handler
            (packet) -> {

            }
    );

    @Override
    public PacketType<?> type() {
        return TYPE;
    }
}
