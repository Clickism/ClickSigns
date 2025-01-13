package me.clickism.clicksigns;

//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}

import me.clickism.clicksigns.block.ModBlocks;
import me.clickism.clicksigns.entity.ModBlockEntityTypes;
import me.clickism.clicksigns.item.ModItemGroups;
import me.clickism.clicksigns.network.RoadSignPacket;
import me.clickism.clicksigns.serialization.RoadSignTemplateReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickSigns implements ModInitializer {
    public static final String MOD_ID = "clicksigns";
    public static final Identifier ERROR_TEMPLATE_ID = identifier("error");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
    }

    public static Identifier identifier(String name) {
        return Identifier.of(MOD_ID, name);
    }
}