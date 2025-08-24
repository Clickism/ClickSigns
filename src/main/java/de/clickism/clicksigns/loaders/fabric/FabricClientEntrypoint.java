/*
 * Copyright 2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.loaders.fabric;

//? if fabric {
import de.clickism.clicksigns.entity.ModBlockEntityTypes;
import de.clickism.clicksigns.entity.RoadSignBlockEntityRenderer;
import de.clickism.clicksigns.network.RoadSignPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class FabricClientEntrypoint implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ModBlockEntityTypes.ROAD_SIGN, RoadSignBlockEntityRenderer::new);
        //? if >=1.20.5 {
        ClientPlayNetworking.registerGlobalReceiver(RoadSignPacket.PAYLOAD_ID, new RoadSignPacket.ClientHandler());
        //?} else
        /*ClientPlayNetworking.registerGlobalReceiver(RoadSignPacket.PACKET_ID, new RoadSignPacket.ClientHandler());*/
    }
}
//?}
