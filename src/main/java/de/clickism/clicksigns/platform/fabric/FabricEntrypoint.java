package de.clickism.clicksigns.platform.fabric;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

/**
 * Fabric entrypoint
 */
public class FabricEntrypoint implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        ClickSigns.initialize();
    }

    @Override
    public void onInitializeClient() {
        BlockEntityRenderers.register(ModBlockEntityTypes.ROAD_SIGN.get(), RoadSignBlockEntityRenderer::new);
    }
}
