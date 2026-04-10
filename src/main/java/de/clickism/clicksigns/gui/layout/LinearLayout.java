package de.clickism.clicksigns.gui.layout;

import net.minecraft.client.gui.layouts.LayoutElement;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple linear layout that supports centering.
 */
public class LinearLayout {
    private final Axis axis;
    private int padding = 0;
    private boolean centerMain = false;
    private boolean centerCross = false;

    private final List<LayoutElement> children = new ArrayList<>();

    /**
     * Creates a new linear layout with the given axis.
     *
     * @param axis the axis to organize the layout in
     */
    protected LinearLayout(Axis axis) {
        this.axis = axis;
    }

    /**
     * Creates a new horizontal linear layout.
     *
     * @return a new horizontal linear layout
     */
    public static LinearLayout horizontal() {
        return new LinearLayout(Axis.HORIZONTAL);
    }

    /**
     * Creates a new vertical linear layout.
     *
     * @return a new vertical linear layout
     */
    public static LinearLayout vertical() {
        return new LinearLayout(Axis.VERTICAL);
    }

    /**
     * Adds an element to the layout.
     *
     * @param element the element to add
     * @return this
     */
    public LinearLayout add(LayoutElement element) {
        children.add(element);
        return this;
    }

    /**
     * Sets the padding between the elements in the layout.
     *
     * @param padding the padding in pixels
     * @return this
     */
    public LinearLayout padding(int padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Centers the elements in both directions.
     *
     * @return this
     */
    public LinearLayout center() {
        this.centerMain = true;
        this.centerCross = true;
        return this;
    }

    /**
     * Centers the elements vertically;
     *
     * @return this
     */
    public LinearLayout centerVertical() {
        if (axis == Axis.VERTICAL) {
            this.centerMain = true;
        } else {
            this.centerCross = true;
        }
        return this;
    }

    /**
     * Centers the elements horizontally;
     *
     * @return this
     */
    public LinearLayout centerHorizontal() {
        if (axis == Axis.HORIZONTAL) {
            this.centerMain = true;
        } else {
            this.centerCross = true;
        }
        return this;
    }

    /**
     * Lays out the elements in the layout starting from the given position.
     *
     * @param x the x position to start from
     * @param y the y position to start from
     */
    public void layout(int x, int y) {
        int currentMain = axis == Axis.HORIZONTAL ? x : y;
        int currentCross = axis == Axis.HORIZONTAL ? y : x;
        // Check centering
        if (centerMain) {
            int totalLength = mainLength();
            currentMain -= totalLength / 2;
        }
        if (centerCross) {
            int maxSideLength = crossLength();
            currentCross -= maxSideLength / 2;
        }
        // Layout elements
        int crossLength = crossLength();
        for (var child : children) {
            if (axis == Axis.HORIZONTAL) {
                child.setX(currentMain);
                child.setY(currentCross);
                currentMain += child.getWidth() + padding;
                if (centerCross) {
                    child.setY(currentCross - child.getHeight() / 2 + crossLength / 2);
                }
            } else {
                child.setX(currentCross);
                child.setY(currentMain);
                currentMain += child.getHeight() + padding;
                if (centerCross) {
                    child.setX(currentCross - child.getWidth() / 2 + crossLength / 2);
                }
            }
        }
    }

    /**
     * Calculate the maximum length of the elements in the cross axis.
     *
     * @return the maximum length of the elements in the cross axis
     */
    private int crossLength() {
        int max = 0;
        for (var child : children) {
            int length = axis == Axis.HORIZONTAL ? child.getWidth() : child.getHeight();
            if (length > max) {
                max = length;
            }
        }
        return max;
    }

    /**
     * Calculate the total length of the elements in the main axis, including padding.
     *
     * @return the total length of the elements in the main axis, including padding
     */
    private int mainLength() {
        int total = 0;
        for (var child : children) {
            total += axis == Axis.HORIZONTAL ? child.getWidth() : child.getHeight();
        }
        return total + padding * (children.size() - 1);
    }

    /**
     * Calculates the width of the layout based on the children and the axis.
     *
     * @return the width of the layout
     */
    public int height() {
        if (axis == Axis.VERTICAL) {
            return mainLength();
        } else {
            return crossLength();
        }
    }

    /**
     * Axis to organize the layout in.
     */
    public enum Axis {
        HORIZONTAL, VERTICAL
    }
}
