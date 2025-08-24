/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.loaders.fabric;

//? if fabric {
//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import de.clickism.clicksigns.util.UpdateNotifier;
import de.clickism.clicksigns.block.ModBlocks;
import de.clickism.clicksigns.entity.ModBlockEntityTypes;
import de.clickism.clicksigns.item.ModItemGroups;
import de.clickism.clicksigns.network.RoadSignPacket;
import de.clickism.clicksigns.serialization.RoadSignTemplateReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.resource.ResourceType;

import static de.clickism.clicksigns.ClickSignsConfig.*;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        ModBlocks.registerBlocks();
        ModItemGroups.registerItemGroups();

        ModBlockEntityTypes.initialize();
        //? if >=1.20.5 {
        PayloadTypeRegistry.playS2C().register(RoadSignPacket.PAYLOAD_ID, RoadSignPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(RoadSignPacket.PAYLOAD_ID, RoadSignPacket.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(RoadSignPacket.PAYLOAD_ID, new RoadSignPacket.ServerHandler());
        //?} else {
        /*ServerPlayNetworking.registerGlobalReceiver(RoadSignPacket.PACKET_ID, new RoadSignPacket.ServerHandler());
        *///?}
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new RoadSignTemplateReloadListener()
        );
        CONFIG.load();
        if (CONFIG.get(CHECK_UPDATE)) {
            ClickSigns.checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> ClickSigns.newerVersion));
        }
    }
}
//?}