package de.clickism.clicksigns.util;

/**
 * Constants class
 */
public class Constants {
    private Constants() {
        // Utility class
    }

    /**
     * The number of pixels are in one block
     */
    public static final float BLOCK_PIXELS = 16f;

    /**
     * The offset to apply to the Z coordinate when rendering to prevent z-fighting
     */
    public static final float Z_FIGHTING_OFFSET = 0.001f;
}
