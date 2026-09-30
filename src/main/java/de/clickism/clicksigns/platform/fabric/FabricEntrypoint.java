package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlockEntityTypes;
import de.clickism.clicksigns.ClickSignsClient;
import de.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

//? if >=26.1 {
import de.clickism.clicksigns.render.SignRenderPipelines;
import net.minecraft.client.renderer.RenderPipelines;
//?}

/**
 * Fabric entrypoint
 */
public class FabricEntrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        ClickSigns.initialize();
        FabricPlatform.INSTANCE.initialize(); // Initialize platform
    }

    @Override
    public void onInitializeClient() {
        ClickSignsClient.initialize();
        BlockEntityRenderers.register(ClickSignsBlockEntityTypes.ROAD_SIGN.get(), RoadSignBlockEntityRenderer::new);
        //? if >=26.1
        SignRenderPipelines.registerAll(RenderPipelines::register);
    }
}
