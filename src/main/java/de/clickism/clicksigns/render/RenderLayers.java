package de.clickism.clicksigns.render;

/**
 * Render layers for the sign renderer.
 */
public interface RenderLayers {
    // Front layers
    int SIGN_FRONT = 1;
    int PLATE_FRONT = 2;
    int SYMBOL = 3;
    int TEXT = 4;

    // Back layers
    int SIGN_BACK = 0;
    int PLATE_BACK = 1;
}
