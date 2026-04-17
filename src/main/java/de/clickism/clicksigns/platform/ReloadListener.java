package de.clickism.clicksigns.platform;

import de.clickism.clicksigns.util.ResourceJsonHandler;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Reload listener interface
 */
public interface ReloadListener extends ResourceJsonHandler {
    /**
     * Called when the resource manager is reloaded
     */
    void onReload(ResourceManager manager);
}
