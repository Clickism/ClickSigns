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
    int SIGN_SURFACE = 2;

    // Back layers
    int SIGN_BACK = -1;
    int PLATE_BACK = -2;
}
