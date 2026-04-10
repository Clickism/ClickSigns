package de.clickism.clicksigns.platform.forge;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static net.minecraftforge.api.distmarker.Dist.CLIENT;
import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

@Mod(ClickSigns.MOD_ID)
@Mod.EventBusSubscriber(modid = ClickSigns.MOD_ID, bus = MOD, value = CLIENT)
public class ForgeEntrypoint {
    public ForgeEntrypoint(FMLJavaModLoadingContext context) {
        ClickSigns.initialize();
        // Initialize forge platform with event bus
        ForgePlatform.INSTANCE.initialize(context.getModEventBus());
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntityTypes.ROAD_SIGN.get(), RoadSignBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }
}
