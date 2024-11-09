package me.clickism.clicksigns;

import me.clickism.clicksigns.entity.ModBlockEntityTypes;
import me.clickism.clicksigns.entity.renderer.RoadSignBlockEntityRenderer;
import me.clickism.clicksigns.network.RoadSignPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ClickSignsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.ROAD_SIGN, RoadSignBlockEntityRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(RoadSignPayload.PAYLOAD_ID, new RoadSignPayload.ClientHandler());
    }
}
