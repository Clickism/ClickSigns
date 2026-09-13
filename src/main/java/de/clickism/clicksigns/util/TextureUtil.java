package de.clickism.clicksigns.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for working with dynamic and resource textures.
 */
public class TextureUtil {
    /**
     * Opens a texture from the given resource location and applies the provided consumer to the resulting NativeImage.
     *
     * @param location the resource location of the texture
     * @param consumer the consumer to apply to the resulting NativeImage
     * @throws RuntimeException if the texture cannot be read or is not yet generated
     */
    public static void processTexture(ResourceLocation location, Consumer<NativeImage> consumer)
        throws RuntimeException {
        processTexture(location, image -> {
            consumer.accept(image);
            return null;
        });
    }

    /**
     * Opens a texture from the given resource location and applies the provided function to the resulting NativeImage.
     *
     * @param location the resource location of the texture
     * @param function the function to apply to the resulting NativeImage
     * @param <T>      the return type of the function
     * @return the result of applying the function to the NativeImage
     * @throws RuntimeException if the texture cannot be read or is not yet generated
     */
    public static <T> T processTexture(ResourceLocation location, Function<NativeImage, T> function)
        throws RuntimeException {
        var minecraft = Minecraft.getInstance();
        var registered = minecraft.getTextureManager().getTexture(location, null);
        // Check if texture is dynamic
        if (registered instanceof DynamicTexture dynamicTexture) {
            var image = dynamicTexture.getPixels();
            if (image == null) {
                // Not yet uploaded/generated
                throw new RuntimeException("Dynamic texture not yet generated: " + location);
            }
            // Owned by the texture manager
            return function.apply(image);
        }
        // Not dynamic, read from resource manager
        try (InputStream stream = minecraft.getResourceManager().open(location);
             NativeImage image = NativeImage.read(stream)) {
            return function.apply(image);
        } catch (IOException e) {
            // Texture couldn't be read
            throw new RuntimeException("Failed to read texture: " + location, e);
        }
    }

    private TextureUtil() {
        // Utility class
    }
}
