/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns;

//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
//?}

import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import me.clickism.clicksigns.util.UpdateNotifier;
import me.clickism.clicksigns.block.ModBlocks;
import me.clickism.clicksigns.entity.ModBlockEntityTypes;
import me.clickism.clicksigns.item.ModItemGroups;
import me.clickism.clicksigns.network.RoadSignPacket;
import me.clickism.clicksigns.serialization.RoadSignTemplateReloadListener;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.MinecraftVersion;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.clickism.clicksigns.ClickSignsConfig.*;

public class ClickSigns implements ModInitializer {
    public static final String MOD_ID = "clicksigns";
    public static final Identifier ERROR_TEMPLATE_ID = identifier("error");
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static String newerVersion = null;

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
            checkUpdates();
            ServerPlayConnectionEvents.JOIN.register(new UpdateNotifier(() -> newerVersion));
        }
    }

    public static Identifier identifier(String name) {
        return Identifier.of(MOD_ID, name);
    }

    private void checkUpdates() {
        String modVersion = FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse(null);
        //? if >= 1.21.6 {
        String minecraftVersion = MinecraftVersion.CURRENT.name();
        //?} else
        /*String minecraftVersion = MinecraftVersion.CURRENT.getName();*/
        new ModrinthUpdateChecker(MOD_ID, "fabric", minecraftVersion).checkVersion(version -> {
            if (modVersion == null || ModrinthUpdateChecker.getRawVersion(modVersion).equals(version)) {
                return;
            }
            newerVersion = version;
            LOGGER.info("Newer version available: {}", version);
        });
    }
}