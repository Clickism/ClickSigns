package de.clickism.clicksigns.sign.template.texture;

import de.clickism.clicksigns.sign.texture.source.TextureSource;

import java.util.List;

public interface TextureOption {
    /**
     * Whether this texture option supports the given dimensions.
     *
     * @param width  width of the texture in pixels
     * @param height height of the texture in pixels
     * @return true if this texture option supports the given dimensions, false otherwise
     */
    boolean supports(int width, int height);

    /**
     * Gets the texture sources for the given dimensions.
     *
     * @param width  width of the texture in pixels
     * @param height height of the texture in pixels
     * @return a list of texture sources for the given dimensions
     */
    List<TextureSource> texturesFor(int width, int height);
}
