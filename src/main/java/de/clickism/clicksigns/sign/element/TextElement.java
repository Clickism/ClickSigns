package de.clickism.clicksigns.sign.element;

import java.awt.*;

/**
 * Text element on a road sign.
 */
public final class TextElement extends RoadSignElement {
    private final String text;
    private final Color color;
    private final float scale;

    /**
     * Creates a new text element with the given properties.
     *
     * @param x     local X coordinate
     * @param y     local Y coordinate
     * @param text  text to display
     * @param color color of the text
     * @param scale scale of the text, where 1.0 is the default size
     */
    public TextElement(int x, int y, String text, Color color, float scale) {
        super(x, y);
        this.text = text;
        this.color = color;
        this.scale = scale;
    }

    /**
     * Gets the text of this element.
     *
     * @return text
     */
    public String text() {
        return text;
    }

    /**
     * Gets the color of this element.
     *
     * @return color
     */
    public Color color() {
        return color;
    }

    /**
     * Gets the scale of this element. A scale of 1.0 means normal size, while a scale of 2.0 means double size.
     *
     * @return scale
     */
    public float scale() {
        return scale;
    }
}
