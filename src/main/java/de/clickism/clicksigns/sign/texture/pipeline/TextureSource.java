package de.clickism.clicksigns.sign.texture.pipeline;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record TextureSource(
    ResourceLocation base,
    // TODO: put colorResolver here, as the base should define it? or actually idk,
    //  maybe make processor have a processContext method, and tilesets can change the colorResolver.
    List<TextureProcessor> processors
) {
    public TextureSource {
        processors = List.copyOf(processors);
    }

    private static final Map<String, Texture> CACHE = new HashMap<>();
    private static final Map<String, ResourceLocation> RESOURCE_LOCATIONS = new HashMap<>();

    /**
     * The error texture to use when loading or generating a texture fails.
     */
    public static final Texture ERROR_TEXTURE =
        new Texture(ClickSigns.identifier("error.png"), 32, 16, null);

    /**
     * Resolves the texture source into a texture by loading the base image and applying all processors in order.
     *
     * @param colorResolver the color resolver to use for processing
     * @return the resolved texture, or the error texture if loading or processing fails
     */
    public Texture resolve(ColorResolver colorResolver) {
        var context = new TextureContext(colorResolver);
        var identity = identity();
        // Check cache
        if (CACHE.containsKey(identity)) {
            return CACHE.get(identity);
        }
        // Generate texture
        var image = resolve(context);
        if (image == null) {
            return ERROR_TEXTURE;
        }
        // Upload texture to Minecraft and cache it
        var location = getOrAssignResourceLocation(identity);
        Image.upload(location, image);
        var texture = new Texture(location, image.width(), image.height(), null);
        CACHE.put(identity, texture);
        return texture;
    }

    /**
     * Resolves the texture source into an image by loading the base image and applying all processors in order.
     *
     * @param context the texture context containing additional information for processing
     * @return the resolved image, or null if loading or processing fails
     */
    private @Nullable Image resolve(TextureContext context) {
        // TODO: Cache!
        var image = Image.open(base);
        if (image == null) {
            return null;
        }
        for (var processor : processors) {
            try {
                image = processor.process(image, context);
            } catch (Exception e) {
                return null;
            }
        }
        return image;
    }

    /**
     * Returns a unique identity string for this texture source and its parameters, used for caching.
     *
     * @return a unique identity string for this texture source
     */
    private String identity() {
        var sb = new StringBuilder();
        sb.append(base.toString());
        for (var processor : processors) {
            sb.append("+").append(processor.identity());
        }
        return sb.toString();
    }

    /**
     * Returns a unique resource location for the given key, generating a new one if it doesn't exist.
     *
     * @param key the key to get or assign a resource location for
     * @return the resource location associated with the key
     */
    private static ResourceLocation getOrAssignResourceLocation(String key) {
        var prefix = "generated/";
        var uuid = UUID.randomUUID();
        return RESOURCE_LOCATIONS.computeIfAbsent(key, k -> ClickSigns.identifier(prefix + uuid));
    }
}
