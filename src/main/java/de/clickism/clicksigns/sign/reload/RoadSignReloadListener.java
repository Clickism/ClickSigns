package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.platform.ReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Reload listener for road sign related data.
 * Provides a common root path.
 */
public interface RoadSignReloadListener extends ReloadListener {
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

    /**
     * Loads all categories from the specified subdirectory and returns a map of directory to category.
     *
     * @param manager       the resource manager to load the categories from
     * @param subDirectory  the subdirectory to load the categories from, relative to the root directory
     * @param categoryClass the class of the category to load, must be deserializable from JSON
     * @param registerer    a consumer that registers the loaded category, called for each loaded category
     * @param <C>           the type of the category
     * @return a map of namespace:directory to category
     */
    default <C> Map<ResourceLocation, C> loadAndRegisterCategories(
            ResourceManager manager,
            String subDirectory,
            Class<C> categoryClass,
            BiConsumer<ResourceLocation, C> registerer
    ) {
        Map<ResourceLocation, C> directoryToCategory = new HashMap<>();
        manager.listResources(
                fromRoot(subDirectory),
                identifier -> isCategoryPath(identifier.getPath())
        ).forEach((location, resource) -> {
            var directory = stripFileName(location.getPath());
            var category = fromJsonOrNull(resource, categoryClass);
            if (category == null) return;
            // Category id is based on the directory
            var categoryId = ResourceLocation.tryBuild(location.getNamespace(), directory);
            directoryToCategory.put(categoryId, category);
            registerer.accept(categoryId, category);
        });
        return directoryToCategory;
    }

    /**
     * Checks if the given path is a category path (ends with "category.json" or "_category.json").
     *
     * @param path the path to check
     * @return true if the path is a category path, false otherwise
     */
    default boolean isCategoryPath(String path) {
        // Enforce underscore in the beginning, so it's always on top in the file tree
        return path.endsWith("_category.json");
    }

    /**
     * Strips the file name from the given path, returning only the directory part.
     *
     * @param path the path to strip the file name from
     * @return the path without the file name, or the original path if it does not contain a slash
     */
    default String stripFileName(String path) {
        var lastSlash = path.lastIndexOf('/');
        if (lastSlash == -1) return path;
        return path.substring(0, lastSlash);
    }
}
