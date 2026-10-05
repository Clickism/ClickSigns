package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.ReloadListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.BiConsumer;

/**
 * Reload listener for road sign related data.
 * Provides a common root path and category logic.
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
        if (path == null || path.isEmpty()) {
            return ROOT_DIR;
        }
        return ROOT_DIR + "/" + path;
    }

    /**
     * Helper method to iterate over all resources in the specified directory that end with the specified suffix,
     * and apply the given consumer to each of them.
     * <p>
     * Catches and logs all exceptions thrown by the consumer, so that one faulty resource does not prevent the others from being loaded.
     *
     * @param manager      resource manager to use
     * @param subDirectory subdirectory to look for resources in, relative to the root directory
     * @param suffix       suffix that the resource path must end with to be included (e.g. ".json")
     * @param consumer     consumer to apply to each resource
     */
    default void forEachResource(
        ResourceManager manager,
        String subDirectory,
        String suffix,
        BiConsumer<Identifier, Resource> consumer) {
        manager.listResources(
            fromRoot(subDirectory),
            identifier -> identifier.getPath().endsWith(suffix)
        ).forEach((location, resource) -> {
            try {
                consumer.accept(location, resource);
            } catch (Exception exception) {
                ClickSigns.LOGGER.error("Error occurred while processing resource {}: {}", location, exception.getMessage(), exception);
            }
        });
    }
}
