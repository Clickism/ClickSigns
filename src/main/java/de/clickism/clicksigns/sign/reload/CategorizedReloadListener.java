package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

/**
 * Reload listener for categorized resources.
 * Provides a common implementation for loading categories and processing resources that belong to those categories.
 *
 * @param <C> they type of the category JSON.
 */
public abstract class CategorizedReloadListener<C> implements RoadSignReloadListener {
    private final CategorizedRegistry<?> registry;
    private final String subDirectory;
    private final String fileSuffix;
    private final Class<C> categoryClass;

    /**
     * Creates a new categorized reload listener.
     *
     * @param registry      the registry to register the categories in
     * @param subDirectory  the subdirectory to load the categories from, relative to the root directory
     * @param fileSuffix    the file suffix to filter resources by, e.g. ".json" or ".png"
     * @param categoryClass the class of the category to load, must be deserializable from JSON
     */
    public CategorizedReloadListener(CategorizedRegistry<?> registry, String subDirectory, String fileSuffix, Class<C> categoryClass) {
        this.registry = registry;
        this.subDirectory = subDirectory;
        this.fileSuffix = fileSuffix;
        this.categoryClass = categoryClass;
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
            registry.createAndRegisterCategory(identifier, name);
        });
        // Process resources
        forEachResource(manager, fromRoot(subDirectory), fileSuffix, (location, resource) -> {
            var categoryId = categoryIdOf(location);
            var category = categories.get(categoryId);
            if (category == null) {
                categoryId = null; // No category
            }
            processResource(location, resource, categoryId, category);
        });
        // Process categories after all resources have been processed
        categories.forEach(this::processCategory);
    }

    /**
     * Gets the category name from the category json.
     *
     * @param category the category JSON to get the name from
     * @return the category name
     */
    protected abstract String categoryName(C category);

    /**
     * Processes a resource.
     *
     * @param location   the location of the resource
     * @param resource   the resource to process
     * @param categoryId the category id of the resource, or null if the resource does not belong to a category
     * @param category   the category of the resource, or null if the resource does not belong to a category
     */
    protected abstract void processResource(
            ResourceLocation location,
            Resource resource,
            @Nullable ResourceLocation categoryId,
            @Nullable C category
    );

    /**
     * Processes a category after all resources have been processed.
     * Can be used to add additional processing after all categories have been loaded
     *
     * @param categoryId the category id of the category to process
     * @param category   the category to process
     */
    protected void processCategory(ResourceLocation categoryId, C category) {
        // Nothing by default
    }
}
