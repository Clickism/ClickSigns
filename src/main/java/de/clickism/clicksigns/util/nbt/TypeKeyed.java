package de.clickism.clicksigns.util.nbt;

/**
 * Interface for objects that have a unique type key for serialization and deserialization.
 */
public interface TypeKeyed {

    /**
     * Unique type key used for serialization and deserialization.
     *
     * @return the type key of this object
     */
    String typeKey();
}
