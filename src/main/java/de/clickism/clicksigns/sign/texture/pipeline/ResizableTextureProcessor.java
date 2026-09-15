package de.clickism.clicksigns.sign.texture.pipeline;

public interface ResizableTextureProcessor extends TextureProcessor {
    /**
     * Resizes this texture processor to output images of the specified width and height.
     *
     * @param width  the desired width of the output image
     * @param height the desired height of the output image
     * @return a new ResizableTextureProcessor instance that produces images of the specified size
     */
    TextureProcessor resize(int width, int height);
}
