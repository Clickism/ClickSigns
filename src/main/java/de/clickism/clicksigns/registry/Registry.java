package de.clickism.clicksigns.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple registry for identifiable objects.
 *
 * @param <T> the type of the registered objects
 */
public class Registry<T extends Identifiable> {
    /**
     * Map from identifier to registered entry
     */
    protected final Map<ResourceLocation, T> entries = new HashMap<>();
    /**
     * The default entry when an identifier is not found
     */
    protected final T defaultEntry;

    /**
     * Creates a new registry with no default entry.
     */
    public Registry() {
        this(null);
    }

    /**
     * Creates a new registry with the given default entry.
     *
     * @param defaultEntry the default entry to return when an identifier is not found, or null to return null
     */
    public Registry(T defaultEntry) {
        this.defaultEntry = defaultEntry;
    }

    /**
     * Registers the given entry.
     * Will override any existing entry with the same identifier.
     *
     * @param entry the entry to register
     */
    public void register(T entry) {
        entries.put(entry.identifier(), entry);
    }

    /**
     * Gets the entry with the given identifier, or the default entry if it doesn't exist.
     * If no default entry is set, will return null.
     *
     * @param id the identifier of the entry to get
     * @return the entry with the given identifier, or the default entry if it doesn't exist
     */
    public T get(ResourceLocation id) {
        return entries.getOrDefault(id, defaultEntry);
    }

    /**
     * Checks if an entry with the given identifier exists in the registry.
     *
     * @param id the identifier to check for
     * @return true if an entry with the given identifier exists, false otherwise
     */
    public boolean has(ResourceLocation id) {
        return entries.containsKey(id);
    }

    /**
     * Gets the entry with the given identifier, or throws an exception if it doesn't exist.
     *
     * @param id the identifier of the entry to get
     * @return the entry with the given identifier
     * @throws IllegalArgumentException if no entry with the given identifier exists
     */
    public T getOrThrow(ResourceLocation id) {
        T entry = entries.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("No entry found for id: " + id);
        }
        return entry;
    }

    /**
     * Gets all registered entries in the registry.
     *
     * @return unmodifiable collection of all registered entries
     */
    public Collection<T> all() {
        return Collections.unmodifiableCollection(entries.values());
    }

    /**
     * Gets all registered identifiers in the registry.
     *
     * @return unmodifiable collection of all registered identifiers
     */
    public Collection<ResourceLocation> allIds() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    /**
     * Clears all entries from the registry.
     */
    public void clear() {
        entries.clear();
    }
}
