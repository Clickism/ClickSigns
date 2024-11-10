package me.clickism.clicksigns.network;

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.entity.RoadSignBlockEntity;
import me.clickism.clicksigns.sign.RoadSign;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record RoadSignPayload(
        BlockPos pos,
        Identifier identifier,
        int textureIndex,
        List<String> texts,
        int alignment
) implements CustomPayload {
    public static final Identifier PACKET_ID = ClickSigns.identifier("road_sign");
    public static final Id<RoadSignPayload> PAYLOAD_ID = new Id<>(PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, RoadSignPayload> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, RoadSignPayload::pos,
            Identifier.PACKET_CODEC, RoadSignPayload::identifier,
            PacketCodecs.INTEGER, RoadSignPayload::textureIndex,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), RoadSignPayload::texts,
            PacketCodecs.INTEGER, RoadSignPayload::alignment,
            RoadSignPayload::new
    );

    public RoadSign toRoadSign() {
        return new RoadSign(identifier, textureIndex, texts, RoadSign.Alignment.values()[alignment]);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }

    public static class ServerHandler implements ServerPlayNetworking.PlayPayloadHandler<RoadSignPayload> {
        @Override
        public void receive(RoadSignPayload payload, ServerPlayNetworking.Context context) {
            ServerWorld world = context.player().getServerWorld();
            if (world.getBlockEntity(payload.pos()) instanceof RoadSignBlockEntity entity) {
                if (payload.identifier().equals(ClickSigns.ERROR_TEMPLATE_ID)) return;
                entity.setRoadSign(payload.toRoadSign());
                PlayerLookup.world(world).forEach(player -> ServerPlayNetworking.send(player, payload));
            }
        }
    }

    public static class ClientHandler implements ClientPlayNetworking.PlayPayloadHandler<RoadSignPayload> {
        @Override
        public void receive(RoadSignPayload payload, ClientPlayNetworking.Context context) {
            MinecraftClient client = context.client();
            client.execute(() -> {
                if (client.world == null) return;
                if (client.world.getBlockEntity(payload.pos()) instanceof RoadSignBlockEntity entity) {
                    entity.setRoadSign(payload.toRoadSign());
                }
            });
        }
    }
}
