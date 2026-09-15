package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.ColorResolver;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for color resolvers.
 * Meant to attach color resolvers to certain textures, such as for tilesets.
 */
public class ColorResolverRegistry {
    private final Map<ResourceLocation, ColorResolver> resolvers = new HashMap<>();

    /**
     * Registers a color resolver for a specific texture.
     *
     * @param texture  the resource location of the texture
     * @param resolver the color resolver to associate with the texture
     */
    public void register(ResourceLocation texture, ColorResolver resolver) {
        resolvers.put(texture, resolver);
    }

    /**
     * Gets the color resolver for a specific texture, or returns a default resolver if none is registered.
     *
     * @param texture the resource location of the texture
     * @return the color resolver associated with the texture, or a default resolver if none is registered
     */
    public ColorResolver getOrDefault(ResourceLocation texture) {
        return resolvers.getOrDefault(texture, ColorResolver.withDefault());
    }

    /**
     * Clears all registered color resolvers.
     */
    public void clear() {
        resolvers.clear();
    }
}
