package de.clickism.clicksigns.render;

/**
 * Render layers for the sign renderer.
 */
public interface RenderLayers {
    // Front layers
    int SIGN_FRONT = 0;
    int PLATE_FRONT = 1;
    int SYMBOL = 2;
    int TEXT = 3;

    // Back layers
    int SIGN_BACK = 0;
    int PLATE_BACK = 1;
}
