package de.clickism.clicksigns.sign.texture.source;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for handling Minecraft images.
 */
public class MinecraftImages {
    private MinecraftImages() {
        // No constructor
    }

    /**
     * Opens an image from the given resource location and returns it as an Image instance.
     *
     * @param location the resource location of the image to open
     * @return an Image instance representing the opened image, or null if the image could not be opened
     */
    public static @Nullable Image open(Identifier location) {
        var minecraft = Minecraft.getInstance();
        try (var nativeImage = NativeImage.read(minecraft.getResourceManager().open(location))) {
            int height = nativeImage.getHeight();
            int width = nativeImage.getWidth();
            var pixels = new int[width * height];
            // Copy all pixels
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    //~ if >=26.1 'getPixelRGBA(' -> 'getPixel('
                    int abgr = nativeImage.getPixel(x, y);

                    int argb = ColorUtil.abgrToArgb(abgr);
                    pixels[y * width + x] = argb;
                }
            }
            return new Image(width, height, pixels);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Uploads the given image to the Minecraft texture manager at the specified resource location.
     *
     * @param location the resource location where the image will be uploaded
     * @param image    the image to upload
     */
    public static void upload(Identifier location, Image image) {
        var minecraft = Minecraft.getInstance();
        var nativeImage = toNativeImage(image);
        var texture = new DynamicTexture(
            //? if >=26.1
            location::toString,
            nativeImage
        );
        minecraft.getTextureManager().register(location, texture);
    }

    /**
     * Converts an Image instance to a NativeImage instance.
     *
     * @param image the Image instance to convert
     * @return a new NativeImage instance representing this Image
     */
    public static NativeImage toNativeImage(Image image) {
        int width = image.width();
        int height = image.height();
        var nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = image.pixelAt(x, y);
                int abgr = ColorUtil.argbToAbgr(argb);

                //~ if >=26.1 'setPixelRGBA(' -> 'setPixel('
                nativeImage.setPixel(x, y, abgr);
            }
        }
        return nativeImage;
    }

}
