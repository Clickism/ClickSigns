package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Reload listener for categorized resources.
 * Provides a common implementation for loading categories and processing resources that belong to those categories.
 *
 * @param <C> they type of the category JSON.
 */
public abstract class CategorizedListener<C> implements SignReloadListener {
    private final CategorizedRegistry<?> registry;
    private final String subDirectory;
    private final Class<C> categoryClass;
    private final Map<String, ResourceProcessor<C>> extensionProcessors = new LinkedHashMap<>();

    /**
     * Creates a new categorized reload listener.
     *
     * @param registry      the registry to register the categories in
     * @param subDirectory  the subdirectory to load the categories from, relative to the root directory
     * @param categoryClass the class of the category to load, must be deserializable from JSON
     */
    public CategorizedListener(
        CategorizedRegistry<?> registry,
        String subDirectory,
        Class<C> categoryClass
    ) {
        this.registry = registry;
        this.subDirectory = subDirectory;
        this.categoryClass = categoryClass;
    }

    /**
     * Checks if the given path is a category path (ends with "category.json" or "_category.json").
     *
     * @param path the path to check
     * @return true if the path is a category path, false otherwise
     */
    protected static boolean isCategoryPath(String path) {
        // Enforce underscore in the beginning, so it's always on top in the file tree
        return path.endsWith("_category.json");
    }

    /**
     * Strips the file name from the given path, returning only the directory part.
     *
     * @param path the path to strip the file name from
     * @return the path without the file name, or the original path if it does not contain a slash
     */
    protected static String stripFileName(String path) {
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
    protected static String stripExtension(String path, String extension) {
        if (path.endsWith(extension)) {
            return path.substring(0, path.length() - extension.length());
        }
        return path;
    }

    /**
     * Strips the given extension from the path of the given Identifier if it ends with it.
     *
     * @param location  the Identifier to strip the extension from
     * @param extension the extension to strip, including the dot (e.g. ".json")
     * @return resource location with stripped extension
     */
    protected static Identifier stripExtension(Identifier location, String extension) {
        var path = stripExtension(location.getPath(), extension);
        return Identifier.tryBuild(location.getNamespace(), path);
    }

    /**
     * Replaces the old extension with the new extension in the path of the given Identifier if it ends with the old extension.
     *
     * @param location     the Identifier to replace the extension in
     * @param oldExtension the extension to replace, including the dot (e.g. ".json")
     * @param newExtension the extension to replace with, including the dot (e.g. ".png")
     * @return resource location with replaced extension
     */
    protected static Identifier replaceExtension(Identifier location, String oldExtension, String newExtension) {
        var path = location.getPath();
        if (path.endsWith(oldExtension)) {
            path = path.substring(0, path.length() - oldExtension.length()) + newExtension;
        }
        return Identifier.tryBuild(location.getNamespace(), path);
    }

    /**
     * Gets the category id for the given resource location by its directory.
     *
     * @param resourceLocation the resource location to get the category id for
     * @return the category id for the given resource location
     */
    protected static Identifier categoryIdOf(Identifier resourceLocation) {
        var directory = stripFileName(resourceLocation.getPath());
        return Identifier.tryBuild(resourceLocation.getNamespace(), directory);
    }

    /**
     * Registers a resource processor for a specific file suffix.
     * The processor will be called for each resource with the given file suffix in the subdirectory.
     *
     * @param fileSuffix the file suffix to register the processor for
     * @param processor  the processor to call for each resource with the given file suffix
     */
    protected void registerProcessor(String fileSuffix, ResourceProcessor<C> processor) {
        extensionProcessors.put(fileSuffix, processor);
    }

    @Override
    public void onReload(ResourceManager manager) {
        registry.clear();
        // Load and parse categories
        var categories = loadAndRegisterCategories(manager, subDirectory, categoryClass, (identifier, json) -> {
            var name = categoryName(json);
            if (name == null) {
                ClickSigns.LOGGER.error("Category {} in {} has no name. Ignoring...", identifier.toString(), subDirectory);
                return;
            }
            var priority = priority(json);
            registry.createAndRegisterCategory(identifier, name, priority);
        });
        // Process resources
        extensionProcessors.forEach((fileSuffix, processor) -> {
            forEachResource(manager, subDirectory, fileSuffix, (location, resource) -> {
                var categoryId = categoryIdOf(location);
                var category = categories.get(categoryId);
                if (category == null) {
                    categoryId = null; // No category
                }
                processor.process(location, resource, categoryId, category);
            });
        });
        // Process categories after all resources have been processed
        categories.forEach(this::processCategory);
    }

    /**
     * Loads all categories from the specified subdirectory and returns a map of directory to category.
     *
     * @param manager       the resource manager to load the categories from
     * @param subDirectory  the subdirectory to load the categories from, relative to the root directory
     * @param categoryClass the class of the category to load, must be deserializable from JSON
     * @param registerer    a consumer that registers the loaded category, called for each loaded category
     * @return a map of namespace:directory to category
     */
    // TODO: Refactor into category tree, with subcategories and show them in the UI that way as well.
    //  Or at the very least, categories should also support subfolders
    protected Map<Identifier, C> loadAndRegisterCategories(
        ResourceManager manager,
        String subDirectory,
        Class<C> categoryClass,
        BiConsumer<Identifier, C> registerer
    ) {
        Map<Identifier, C> directoryToCategory = new HashMap<>();
        manager.listResources(
            fromRoot(subDirectory),
            identifier -> isCategoryPath(identifier.getPath())
        ).forEach((location, resource) -> {
            var directory = stripFileName(location.getPath());
            var category = fromJsonOrNull(resource, categoryClass);
            if (category == null) return;
            // Category id is based on the directory
            var categoryId = Identifier.tryBuild(location.getNamespace(), directory);
            directoryToCategory.put(categoryId, category);
            registerer.accept(categoryId, category);
        });
        return directoryToCategory;
    }

    /**
     * Gets the category name from the category json.
     *
     * @param category the category JSON to get the name from
     * @return the category name
     */
    protected abstract String categoryName(C category);

    /**
     * Gets the priority of the category.
     *
     * @param category the category to get the priority of
     * @return the priority of the category
     */
    protected abstract int priority(C category);

    /**
     * Processes a category after all resources have been processed.
     * Can be used to add additional processing after all categories have been loaded
     *
     * @param categoryId the category id of the category to process
     * @param category   the category to process
     */
    protected void processCategory(Identifier categoryId, C category) {
        // Nothing by default
    }

    /**
     * Processes a resource with the given location and resource.
     * The category id and category are provided if the resource belongs to a category.
     */
    public interface ResourceProcessor<C> {
        void process(
            Identifier location,
            Resource resource,
            @Nullable Identifier categoryId,
            @Nullable C category
        );
    }
}
