package de.clickism.clicksigns.sign.texture.source;

import com.mojang.blaze3d.platform.NativeImage;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.TileSet;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.generator.CachedTextureGenerator;
import de.clickism.clicksigns.sign.texture.generator.TextureTiler;
import de.clickism.clicksigns.util.PixelSized;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import org.jetbrains.annotations.Nullable;

/**
 * Texture source that generates a texture by tiling a given tileset to fit the specified dimensions.
 *
 * @param tileSetId resource location of the tileset to use for tiling
 * @param width     width of the generated texture in pixels
 * @param height    height of the generated texture in pixels
 */
public record TiledTextureSource(
    ResourceLocation tileSetId,
    int width,
    int height
) implements TextureSource, PixelSized {
    /**
     * Type key
     */
    public static final String TYPE = "tiled";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public Texture resolve(ColorResolver colorResolver) {
        var tileSet = resolveTileSet();
        if (tileSet == null) return ERROR_TEXTURE;
        var texture = new TextureTiler(tileSet, width, height).getOrGenerate();
        if (texture == null) return ERROR_TEXTURE;
        return texture;
    }

    /**
     * Returns the color of the pixel at the center of the generated texture,
     * or null if the texture could not be generated or the tileset is not found.
     *
     * @return the color of the center pixel in ARGB format, or null if not available
     */
    public @Nullable Integer primaryColor() {
        var tileSet = resolveTileSet();
        if (tileSet == null) return null;
        try (var image = CachedTextureGenerator.openImage(tileSet.identifier())) {
            int centerX = image.getWidth() / 2;
            int centerY = image.getHeight() / 2;

            int pixel = image.getPixelRGBA(centerX, centerY);
            // Convert RGBA to ARGB
            return FastColor.ARGB32.color(
                FastColor.ABGR32.alpha(pixel),
                FastColor.ABGR32.red(pixel),
                FastColor.ABGR32.green(pixel),
                FastColor.ABGR32.blue(pixel)
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public TextureSource resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return this; // No resizing needed
        }
        return new TiledTextureSource(tileSetId, width, height);
    }

    @Override
    public boolean canResize() {
        return true;
    }

    /**
     * Creates a new tiled texture source that has unknown size.
     * Meant to be used as a placeholder until {@link #resize(int, int)} is called.
     *
     * @param tileSetId the resource location of the tileset to use for tiling
     * @return a new TiledTextureSource with the specified tileset and unknown size
     */
    public static TiledTextureSource unsized(ResourceLocation tileSetId) {
        return new TiledTextureSource(tileSetId, 0, 0);
    }

    /**
     * Resolves the tileset for this texture source.
     *
     * @return the resolved tileset, or null if not found
     */
    public @Nullable TileSet resolveTileSet() {
        return SignRegistries.TILE_SETS.get(tileSetId);
    }
}
