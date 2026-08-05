package de.clickism.clicksigns.platform.neoforge;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlockEntityTypes;
import de.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static net.neoforged.api.distmarker.Dist.CLIENT;

@Mod(ClickSigns.MOD_ID)
@EventBusSubscriber(modid = ClickSigns.MOD_ID, value = CLIENT)
public class NeoForgeEntrypoint {
    public NeoForgeEntrypoint(IEventBus eventBus) {
        ClickSigns.initialize();
        // Initialize forge platform with event bus
        NeoForgePlatform.INSTANCE.initialize(eventBus);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ClickSignsBlockEntityTypes.ROAD_SIGN.get(), RoadSignBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("0");

        NeoForgeNetwork.INSTANCE.register(registrar);
    }
}
