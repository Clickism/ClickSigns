package de.clickism.clicksigns;

import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.platform.network.PacketRegistry;
import de.clickism.clicksigns.sign.reload.SignReloadListener;
import de.clickism.modrinthupdatechecker.ModrinthUpdateChecker;
import net.minecraft.DetectedVersion;
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
     * Capitalized mod id of ClickSigns
     */
    public static final String CAPITALIZED_MOD_ID = "ClickSigns";
    /**
     * Main logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(CAPITALIZED_MOD_ID);

    public static final UpdateNotifier UPDATE_NOTIFIER = new UpdateNotifier();

    /**
     * Initializes the server mod.
     */
    public static void initialize() {
        ClickSignsBlocks.initialize();
        ClickSignsBlockEntityTypes.initialize();
        PacketRegistry.register(RoadSignUpdatePacket.TYPE);
        Platform.network().registerServer(); // Register network

        // Load config
        ClickSignsConfig.CONFIG.load();

        // Check for updates
        if (ClickSignsConfig.CHECK_UPDATES.get()) {
            ClickSigns.LOGGER.info("Checking for updates...");
            checkUpdates();
        }
    }

    /**
     * Checks for updates on Modrinth and notifies the user if a newer version is available.
     */
    private static void checkUpdates() {
        var minecraftVersion = DetectedVersion.BUILT_IN.getName();
        var loader = Platform.get().name();
        ModrinthUpdateChecker.loader(MOD_ID, loader)
            .minecraftVersion(minecraftVersion)
            .includeChangelog(true)
            .onVersion(version -> {
                var current = Platform.get().modVersion(MOD_ID);
                if (version.versionNumber().equals(current)) return;
                // Newer version
                UPDATE_NOTIFIER.newerVersion(version);
                var message = "\n\nNewer version available for " + CAPITALIZED_MOD_ID
                              + ": " + version.strippedVersionNumber()
                              + "\nChangelog:\n" + version.changelog();
                LOGGER.info(message.indent(4));
            })
            .check();
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

    /**
     * Create a resource location for a sign asset, with the signs root directory as prefix
     *
     * @param path the path of the sign asset, relative to the signs root directory
     * @return the resource location for the sign asset
     */
    public static ResourceLocation signAsset(String path) {
        return identifier(SignReloadListener.ROOT_DIR + "/" + path);
    }
}
