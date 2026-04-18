package de.clickism.clicksigns.sign;

import org.joml.Vector2f;

/**
 * Alignment for rendering elements
 */
public enum Alignment {
    TOP_LEFT(-1, 1),
    TOP_CENTER(0, 1),
    TOP_RIGHT(1, 1),
    CENTER_LEFT(-1, 0),
    CENTER(0, 0),
    CENTER_RIGHT(1, 0),
    BOTTOM_LEFT(-1, -1),
    BOTTOM_CENTER(0, -1),
    BOTTOM_RIGHT(1, -1);

    private final Vector2f offset;

    Alignment(float offsetX, float offsetY) {
        this.offset = new Vector2f(offsetX, offsetY);
    }

    /**
     * The offset to apply to the position when rendering,
     * where (0, 0) is the center and i.E (1, 1) is the bottom right corner.
     *
     * @return offset
     */
    public Vector2f offset() {
        return this.offset;
    }
}
