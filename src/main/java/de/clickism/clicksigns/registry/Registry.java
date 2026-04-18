package de.clickism.clicksigns.registry;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Registry<T extends Identifiable> {
    protected final Map<ResourceLocation, T> entries = new HashMap<>();

    public void register(T entry) {
        entries.put(entry.identifier(), entry);
    }

    public @Nullable T get(ResourceLocation id) {
        return entries.get(id);
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
