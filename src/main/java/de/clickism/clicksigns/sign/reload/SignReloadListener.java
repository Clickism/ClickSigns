package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.platform.ReloadListener;

/**
 * Reload listener for road sign related data.
 * Provides a common root path.
 */
public interface SignReloadListener extends ReloadListener {
    /**
     * Root path for all road sign related textures/data.
     */
    String ROOT_DIR = "signs";

    /**
     * Helper method to create a path relative to the root path.
     *
     * @param path the path relative to the root path
     * @return the full path with the root path as prefix
     */
    default String fromRoot(String path) {
        return ROOT_DIR + "/" + path;
    }
}
