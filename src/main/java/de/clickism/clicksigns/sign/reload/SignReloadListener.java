package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.platform.ReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;
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

    /**
     * Strips the given extension from the path if it ends with it, otherwise returns the original path.
     *
     * @param path      the path to strip the extension from
     * @param extension the extension to strip, including the dot (e.g. ".json")
     * @return the path without the extension if it ends with it, otherwise the original path
     */
    default String stripExtension(String path, String extension) {
        if (path.endsWith(extension)) {
            return path.substring(0, path.length() - extension.length());
        }
        return path;
    }

    /**
     * Strips the given extension from the path of the given ResourceLocation if it ends with it.
     *
     * @param location  the ResourceLocation to strip the extension from
     * @param extension the extension to strip, including the dot (e.g. ".json")
     * @return resource location with stripped extension
     */
    default ResourceLocation stripExtension(ResourceLocation location, String extension) {
        var path = stripExtension(location.getPath(), extension);
        return new ResourceLocation(location.getNamespace(), path);
    }

    /**
     * Replaces the old extension with the new extension in the path of the given ResourceLocation if it ends with the old extension.
     *
     * @param location     the ResourceLocation to replace the extension in
     * @param oldExtension the extension to replace, including the dot (e.g. ".json")
     * @param newExtension the extension to replace with, including the dot (e.g. ".png")
     * @return resource location with replaced extension
     */
    default ResourceLocation replaceExtension(ResourceLocation location, String oldExtension, String newExtension) {
        var path = location.getPath();
        if (path.endsWith(oldExtension)) {
            path = path.substring(0, path.length() - oldExtension.length()) + newExtension;
        }
        return new ResourceLocation(location.getNamespace(), path);
    }

    /**
     * Gets the category id for the given resource location by its directory.
     *
     * @param resourceLocation the resource location to get the category id for
     * @return the category id for the given resource location
     */
    default ResourceLocation categoryIdOf(ResourceLocation resourceLocation) {
        var directory = stripFileName(resourceLocation.getPath());
        return ResourceLocation.tryBuild(resourceLocation.getNamespace(), directory);
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
            BiConsumer<ResourceLocation, Resource> consumer) {
        manager.listResources(
                fromRoot(subDirectory),
                identifier -> identifier.getPath().endsWith(suffix)
        ).forEach((location, resource) -> {
            try {
                consumer.accept(location, resource);
            } catch (Exception exception) {
                ClickSigns.LOGGER.error("Error occurred while processing resource {}: {}", location.toString(), exception.getMessage(), exception);
            }
        });
    }
}
