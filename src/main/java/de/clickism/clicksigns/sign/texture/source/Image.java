package de.clickism.clicksigns.sign.texture.source;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an image with its dimensions and pixel data.
 *
 * @param width  the width of the image in pixels
 * @param height the height of the image in pixels
 * @param pixels the pixel data of the image, stored as a one-dimensional array of ARGB integers
 */
public record Image(
    int width,
    int height,
    int[] pixels
) {
    /**
     * Creates a new Image with the specified width and height, initializing the pixel data to an empty array.
     *
     * @param width  the width of the image in pixels
     * @param height the height of the image in pixels
     */
    public Image(int width, int height) {
        this(width, height, new int[width * height]);
    }

    public int pixelAt(int x, int y) {
        if (!withinBounds(x, y)) {
            throw new IndexOutOfBoundsException(
                "Pixel coordinates out of bounds: (" + x + ", " + y + ") for image of size " + width + "x" + height
            );
        }
        return pixels[y * width + x];
    }

    public void setPixelAt(int x, int y, int color) {
        if (!withinBounds(x, y)) {
            throw new IndexOutOfBoundsException(
                "Pixel coordinates out of bounds: (" + x + ", " + y + ") for image of size " + width + "x" + height
            );
        }
        pixels[y * width + x] = color;
    }

    public boolean withinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void forEachPixel(PixelConsumer consumer) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = pixelAt(x, y);
                consumer.accept(x, y, color);
            }
        }
    }

    public Image copy() {
        return new Image(width, height, pixels.clone());
    }

    public NativeImage toNativeImage() {
        var nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelAt(x, y);
                int abgr = FastColor.ABGR32.color(
                    FastColor.ARGB32.alpha(argb),
                    FastColor.ARGB32.blue(argb),
                    FastColor.ARGB32.green(argb),
                    FastColor.ARGB32.red(argb)
                );
                nativeImage.setPixelRGBA(x, y, abgr);
            }
        }
        return nativeImage;
    }

    public static @Nullable Image open(ResourceLocation location) {
        var minecraft = Minecraft.getInstance();
        try (var nativeImage = NativeImage.read(minecraft.getResourceManager().open(location))) {
            int height = nativeImage.getHeight();
            int width = nativeImage.getWidth();
            var pixels = new int[width * height];
            // Copy all pixels
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int abgr = nativeImage.getPixelRGBA(x, y);
                    int argb = FastColor.ARGB32.color(
                        FastColor.ABGR32.alpha(abgr),
                        FastColor.ABGR32.red(abgr),
                        FastColor.ABGR32.green(abgr),
                        FastColor.ABGR32.blue(abgr)
                    );
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
    public static void upload(ResourceLocation location, Image image) {
        var minecraft = Minecraft.getInstance();
        var nativeImage = image.toNativeImage();
        minecraft.getTextureManager().register(location, new DynamicTexture(nativeImage));
    }

    @FunctionalInterface
    public interface PixelConsumer {
        void accept(int x, int y, int color);
    }
}
