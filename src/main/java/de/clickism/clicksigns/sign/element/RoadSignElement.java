package de.clickism.clicksigns.sign.element;

/**
 * An element that can be placed on a road sign.
 */
public abstract sealed class RoadSignElement permits TextElement, SymbolElement {
    private final int localX;
    private final int localY;

    /**
     * Creates a new road sign element at the given local coordinates.
     *
     * @param localX Local X coordinate
     * @param localY Local Y coordinate
     */
    public RoadSignElement(int localX, int localY) {
        this.localX = localX;
        this.localY = localY;
    }

    /**
     * Gets the local X coordinate of this element.
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0, 0) being the bottom-left corner.
     *
     * @return Local X coordinate
     */
    public int localX() {
        return localX;
    }

    /**
     * Gets the local Y coordinate of this element.
     * Local coordinates are relative to the bottom-left corner of the road sign, with (0
     *
     * @return Local Y coordinate
     */
    public int localY() {
        return localY;
    }
}
