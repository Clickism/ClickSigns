package de.clickism.clicksigns.sign.texture.pipeline;

public interface TextureProcessor {
    /**
     * Processes the given input image and returns the processed image.
     *
     * @param input   the input image to process
     * @param context the texture context containing additional information for processing
     * @return the processed image
     */
    Image process(Image input, TextureContext context);

    /**
     * Returns a unique identity string for this texture processor and its parameters,
     * used for caching.
     *
     * @return a unique identity string for this texture processor
     */
    String identity();
}
