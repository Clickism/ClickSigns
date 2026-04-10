package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.Platform;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Reload listener for road sign related data
 */
public class RoadSignReloadListener implements Platform.ReloadListener {
    @Override
    public void onReload(ResourceManager manager) {
        manager.listResources(
                "roadsigns",
                identifier -> identifier.getPath().endsWith(".json")
        ).forEach((identifier, resource) -> {
            ClickSigns.LOGGER.info("Loading road sign from resource " + identifier.toString());
        });
    }
}
