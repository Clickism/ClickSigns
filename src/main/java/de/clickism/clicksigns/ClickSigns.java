package de.clickism.clicksigns;

import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.network.PacketRegistry;
import de.clickism.clicksigns.registry.ModBlockEntityTypes;
import de.clickism.clicksigns.registry.ModBlocks;
import de.clickism.clicksigns.sign.RoadSignReloadListener;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod class
 */
public class ClickSigns {
    /**
     * Mod id of ClickSigns
     */
    public static final String MOD_ID = "clicksigns";
    /**
     * Main logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /**
     * Initializes the mod, registers block, block entity types, packets and reload listeners
     */
    public static void initialize() {
        ModBlocks.initialize();
        ModBlockEntityTypes.initialize();
        PacketRegistry.register(RoadSignUpdatePacket.TYPE);
        Platform.network().register(); // Register network
        // Add reload listener
        Platform.get().addReloadListener(new RoadSignReloadListener());
    }

    /**
     * Create a resource location with the mod id as namespace
     *
     * @param path the path of the resource location
     * @return the resource location
     */
    public static ResourceLocation identifier(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }
}
