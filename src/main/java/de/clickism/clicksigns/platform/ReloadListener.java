package de.clickism.clicksigns.platform;

import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Reload listener interface
 */
public interface ReloadListener extends JsonHandler {
    /**
     * Called when the resource manager is reloaded
     */
    void onReload(ResourceManager manager);
}
