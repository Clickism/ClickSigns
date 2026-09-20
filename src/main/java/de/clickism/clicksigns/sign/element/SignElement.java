package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.serialization.TypeKeyed;
import de.clickism.clicksigns.sign.Alignment;

/**
 * Represents an element of a road sign.
 * <p>
 * Elements are positioned using the sign's coordinate system, where (0, 0) is the bottom left corner of the sign.
 * For more information, see {@link de.clickism.clicksigns.sign.RoadSign}.
 */
public sealed interface SignElement extends TypeKeyed
    permits PlateElement, SymbolElement, TextElement {
    /**
     * Gets the X coordinate of this element.
     *
     * @return X coordinate
     */
    int x();

    /**
     * Gets the Y coordinate of this element.
     *
     * @return Y coordinate
     */
    int y();

    /**
     * Gets the width of this element in sign space.
     *
     * @return Width of this element in sign space
     */
    float width();

    /**
     * Gets the height of this element in sign space.
     *
     * @return Height of this element in sign space
     */
    float height();

    /**
     * Returns the aligned X coordinate of this element in sign space, which is a floating point number.
     *
     * @return The aligned X coordinate of this element in sign space
     */
    default float alignedX() {
        float x = x();
        float width = width();
        // Center origin
        x -= width / 2f;
        // Align
        var offset = alignment().offset();
        x += offset.x * (width / 2f);
        return x;
    }

    /**
     * Returns the aligned Y coordinate of this element in sign space, which is a floating point number.
     *
     * @return The aligned Y coordinate of this element in sign space
     */
    default float alignedY() {
        float y = y();
        float height = height();
        // Center origin
        y -= height / 2f;
        // Align
        var offset = alignment().offset();
        y += offset.y * (height / 2f);
        return y;
    }

    /**
     * Gets the alignment of this element. The alignment determines how the element should be positioned.
     *
     * @return the alignment of this element
     */
    Alignment alignment();

    /**
     * Creates a new element with the given local coordinates, keeping the other properties the same.
     *
     * @param x local X coordinate
     * @param y local Y coordinate
     * @return a new element with the given local coordinates, keeping the other properties the same
     */
    SignElement withPosition(int x, int y);

    /**
     * Creates a new element with the given alignment, keeping the other properties the same.
     *
     * @param alignment the new alignment
     * @return a new element with the given alignment, keeping the other properties the same
     */
    SignElement withAlignment(Alignment alignment);
}
