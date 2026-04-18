package de.clickism.clicksigns.registry;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Registry<T extends Identifiable> {
    protected final Map<ResourceLocation, T> entries = new HashMap<>();
    protected final T defaultEntry;

    public Registry() {
        this(null);
    }

    public Registry(T defaultEntry) {
        this.defaultEntry = defaultEntry;
    }

    public void register(T entry) {
        entries.put(entry.identifier(), entry);
    }

    public T get(ResourceLocation id) {
        return entries.getOrDefault(id, defaultEntry);
    }

    public T getOrThrow(ResourceLocation id) {
        T entry = entries.get(id);
        if (entry == null) {
            throw new IllegalArgumentException("No entry found for id: " + id);
        }
        return entry;
    }

    public Collection<T> all() {
        return Collections.unmodifiableCollection(entries.values());
    }

    public Collection<ResourceLocation> allIds() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    public void clear() {
        entries.clear();
    }
}
