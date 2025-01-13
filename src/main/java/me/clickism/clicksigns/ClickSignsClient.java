package me.clickism.clicksigns;

import me.clickism.clicksigns.entity.ModBlockEntityTypes;
import me.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import me.clickism.clicksigns.network.RoadSignPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ClickSignsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.ROAD_SIGN, RoadSignBlockEntityRenderer::new);
        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(RoadSignPacket.PAYLOAD_ID, new RoadSignPacket.ClientHandler());
        //?} else
        /*ClientPlayNetworking.registerGlobalReceiver(RoadSignPacket.PACKET_ID, new RoadSignPacket.ClientHandler());*/
    }
}
