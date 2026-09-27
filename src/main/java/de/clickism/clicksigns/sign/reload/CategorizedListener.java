package de.clickism.clicksigns.sign.reload;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

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
    protected void processCategory(ResourceLocation categoryId, C category) {
        // Nothing by default
    }

    /**
     * Processes a resource with the given location and resource.
     * The category id and category are provided if the resource belongs to a category.
     */
    public interface ResourceProcessor<C> {
        void process(
            ResourceLocation location,
            Resource resource,
            @Nullable ResourceLocation categoryId,
            @Nullable C category
        );
    }
}
