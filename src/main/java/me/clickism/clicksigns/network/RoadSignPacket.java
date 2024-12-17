package me.clickism.clicksigns.network;

//? if >=1.20.5 {
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
//?} else {
/*import net.fabricmc.fabric.api.networking.v1.PacketSender;
import io.netty.buffer.Unpooled;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
*///?}

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.entity.RoadSignBlockEntity;
import me.clickism.clicksigns.sign.RoadSign;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public record RoadSignPacket(
        BlockPos pos,
        Identifier identifier,
        int textureIndex,
        List<String> texts,
        int alignment
) /*? if >=1.20.5 {*/ implements CustomPayload /*?}*/ {
    public static final Identifier PACKET_ID = ClickSigns.identifier("road_sign");
    //? if >=1.20.5 {
    public static final Id<RoadSignPacket> PAYLOAD_ID = new Id<>(PACKET_ID);

    @Override
    public Id<? extends CustomPayload> getId() {
        return PAYLOAD_ID;
    }

    public static final PacketCodec<RegistryByteBuf, RoadSignPacket> CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, RoadSignPacket::pos,
            Identifier.PACKET_CODEC, RoadSignPacket::identifier,
            PacketCodecs.INTEGER, RoadSignPacket::textureIndex,
            PacketCodecs.STRING.collect(PacketCodecs.toList()), RoadSignPacket::texts,
            PacketCodecs.INTEGER, RoadSignPacket::alignment,
            RoadSignPacket::new
    );
    //?}
    
    public static class ServerHandler implements 
            /*? if >=1.20.5 {*/ 
            ServerPlayNetworking.PlayPayloadHandler<RoadSignPacket>
            /*?} else {*/ 
            /*ServerPlayNetworking.PlayChannelHandler 
            *//*?}*/
    {
        @Override
        public void receive(
                /*? if >=1.20.5 {*/ 
                RoadSignPacket packet, ServerPlayNetworking.Context context
                /*?} else {*/ 
                /*MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                PacketByteBuf buf, PacketSender packetSender
                *//*?}*/
    ) {
            //? if >=1.20.5 {
            ServerWorld world = context.player().getServerWorld();
            //?} else {
            /*ServerWorld world = player.getServerWorld();
            RoadSignPacket packet = fromPacketByteBuf(buf);
            *///?}
            world.getServer().execute(() -> {
                if (world.getBlockEntity(packet.pos()) instanceof RoadSignBlockEntity entity) {
                    if (packet.identifier().equals(ClickSigns.ERROR_TEMPLATE_ID)) return;
                    entity.setRoadSign(packet.toRoadSign());
                    PlayerLookup.world(world).forEach(p ->
                                    //? if >=1.20.5 {
                                    ServerPlayNetworking.send(p, packet)
                                     //?} else {
                                    /*ServerPlayNetworking.send(p, PACKET_ID, packet.toPacketByteBuf())
                            *///?}
                    );
                }
            });
        }
    }

    public static class ClientHandler implements
            /*? if >=1.20.5 {*/ 
            ClientPlayNetworking.PlayPayloadHandler<RoadSignPacket>
            /*?} else {*/ 
            /*ClientPlayNetworking.PlayChannelHandler 
            *//*?}*/
    {
        @Override
        public void receive(
                /*? if >=1.20.5 {*/ 
                RoadSignPacket packet, ClientPlayNetworking.Context context
                /*?} else {*/
                /*MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender
                *//*?}*/
        ) {
            //? if >=1.20.5
            MinecraftClient client = context.client();
            //? if <1.20.5
            /*RoadSignPacket packet = fromPacketByteBuf(buf);*/
            client.execute(() -> {
                if (client.world == null) return;
                if (client.world.getBlockEntity(packet.pos()) instanceof RoadSignBlockEntity entity) {
                    entity.setRoadSign(packet.toRoadSign());
                }
            });
        }
    }
    
    //? if <1.20.5 {
    /*public PacketByteBuf toPacketByteBuf() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeBlockPos(pos);
        buf.writeIdentifier(identifier);
        buf.writeInt(textureIndex);
        buf.writeCollection(texts, PacketByteBuf::writeString);
        buf.writeInt(alignment);
        return buf;
    }
    
    private static RoadSignPacket fromPacketByteBuf(PacketByteBuf buf) {
        return new RoadSignPacket(
                buf.readBlockPos(),
                buf.readIdentifier(),
                buf.readInt(),
                buf.readList(PacketByteBuf::readString),
                buf.readInt()
        );
    }
    *///?}

    public RoadSign toRoadSign() {
        return new RoadSign(identifier, textureIndex, texts, RoadSign.Alignment.values()[alignment]);
    }
}