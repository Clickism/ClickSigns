package de.clickism.clicksigns.platform.forge;

import de.clickism.clicksigns.ClickSigns;
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

        var bus = context.getModEventBus();
        ForgePlatform.ITEMS_REGISTRY.register(bus);
        ForgePlatform.BLOCKS_REGISTRY.register(bus);
        ForgePlatform.BLOCK_ENTITY_TYPE_REGISTRY.register(bus);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

    }
}
