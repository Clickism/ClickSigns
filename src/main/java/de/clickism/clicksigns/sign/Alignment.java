package de.clickism.clicksigns.sign;

import org.joml.Vector2f;

import java.util.List;

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

    // Text alignments
    public static final Alignment TEXT_LEFT = TOP_LEFT;
    public static final Alignment TEXT_CENTER = TOP_CENTER;
    public static final Alignment TEXT_RIGHT = TOP_RIGHT;

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
        return new Vector2f(offset);
    }

    /**
     * Returns the opposite alignment.
     *
     * @return the opposite alignment
     */
    public Alignment opposite() {
        return switch (this) {
            case TOP_LEFT -> BOTTOM_RIGHT;
            case TOP_CENTER -> BOTTOM_CENTER;
            case TOP_RIGHT -> BOTTOM_LEFT;
            case CENTER_LEFT -> CENTER_RIGHT;
            case CENTER -> CENTER;
            case CENTER_RIGHT -> CENTER_LEFT;
            case BOTTOM_LEFT -> TOP_RIGHT;
            case BOTTOM_CENTER -> TOP_CENTER;
            case BOTTOM_RIGHT -> TOP_LEFT;
        };
    }

    /**
     * Returns a list of all alignments.
     *
     * @return list of all alignments
     */
    public static List<Alignment> all() {
        return List.of(values());
    }

    /**
     * Returns a list of all text alignments.
     *
     * @return list of all text alignments
     */
    public static List<Alignment> textAlignments() {
        return List.of(TEXT_LEFT, TEXT_CENTER, TEXT_RIGHT);
    }
}
