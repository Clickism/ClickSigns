package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.Alignment;

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
     * @param x         local X coordinate
     * @param y         local Y coordinate
     * @param alignment alignment of the text
     * @param text      text to display
     * @param color     color of the text
     * @param scale     scale of the text, where 1.0 is the default size
     */
    public TextElement(int x, int y, Alignment alignment, String text, Color color, float scale) {
        super(x, y, alignment);
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

    public TextElement withText(String text) {
        return new TextElement(localX(), localY(), alignment(), text, color, scale);
    }

    public TextElement withColor(Color color) {
        return new TextElement(localX(), localY(), alignment(), text, color, scale);
    }

    public TextElement withScale(float scale) {
        return new TextElement(localX(), localY(), alignment(), text, color, scale);
    }

    public TextElement withPosition(int localX, int localY) {
        return new TextElement(localX, localY, alignment(), text, color, scale);
    }

    public TextElement withAlignment(Alignment alignment) {
        return new TextElement(localX(), localY(), alignment, text, color, scale);
    }
}
