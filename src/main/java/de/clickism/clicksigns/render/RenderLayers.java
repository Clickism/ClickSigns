package de.clickism.clicksigns.render;

/**
 * Render layers for the sign renderer.
 */
public interface RenderLayers {
    /**
     * How much to offset the Z position between layers to avoid Z-fighting.
     */
    float Z_FIGHTING_OFFSET = 0.001f;

    // Front layers
    int SIGN_FRONT = 1;
    int PLATE_FRONT = 2;
    int SYMBOL = 3;
    int TEXT = 4;

    // Back layers
    int SIGN_BACK = -1;
    int PLATE_BACK = -2;
}
