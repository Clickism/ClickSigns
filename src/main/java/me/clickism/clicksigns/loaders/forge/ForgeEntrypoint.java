/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */
//? if forge {
package me.clickism.clicksigns.loaders.forge;

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.block.ModBlocks;
import me.clickism.clicksigns.entity.ModBlockEntityTypes;
import me.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import me.clickism.clicksigns.item.ModItemGroups;
import me.clickism.clicksigns.network.RoadSignPacket;
import me.clickism.clicksigns.serialization.RoadSignTemplateReloadListener;
import me.clickism.clicksigns.util.UpdateNotifier;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static me.clickism.clicksigns.ClickSignsConfig.*;

@Mod("clicksigns")
public class ForgeEntrypoint {
    public ForgeEntrypoint() throws ClassNotFoundException {
        ModBlocks.registerBlocks();
        ModItemGroups.registerItemGroups();
        ModBlockEntityTypes.initialize();

        //? if >=1.20.5 {
        /*PayloadTypeRegistry.playS2C().register(RoadSignPacket.PAYLOAD_ID, RoadSignPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(RoadSignPacket.PAYLOAD_ID, RoadSignPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(RoadSignPacket.PAYLOAD_ID, new RoadSignPacket.ServerHandler());
        *///?} else {
        ServerPlayNetworking.registerGlobalReceiver(RoadSignPacket.PACKET_ID, new RoadSignPacket.ServerHandler());
        //?}
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new RoadSignTemplateReloadListener()
        );
        CONFIG.load();
        if (CONFIG.get(CHECK_UPDATE)) {
            ClickSigns.checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> ClickSigns.newerVersion));
        }
    }

    @Mod.EventBusSubscriber(modid = ClickSigns.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntityTypes.ROAD_SIGN.get(), RoadSignBlockEntityRenderer::new);
        }

        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            //? if >=1.20.5 {
            /*ClientPlayNetworking.registerGlobalReceiver(RoadSignPacket.PAYLOAD_ID, new RoadSignPacket.ClientHandler());
             *///?} else
            ClientPlayNetworking.registerGlobalReceiver(RoadSignPacket.PACKET_ID, new RoadSignPacket.ClientHandler());
        }
    }
}
//?}
