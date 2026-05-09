package de.clickism.clicksigns.registry;

import net.minecraft.resources.ResourceLocation;

/**
 * An interface for objects that have a unique identifier.
 */
public interface Identifiable {
    /**
     * Returns the unique identifier of this object.
     *
     * @return the unique identifier of this object
     */
    ResourceLocation identifier();
}
