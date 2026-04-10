package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.util.Alignment;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Text element on a road sign.
 *
 * @param localX          local X coordinate
 * @param localY          local Y coordinate
 * @param alignment       alignment of the text
 * @param text            text to display
 * @param scale           scale of the text, where 1.0 is the default size
 * @param color           RGBA color of the text
 * @param backgroundColor RGBA color of the text background, or 0 for no background
 */
public record TextElement(
        int localX,
        int localY,
        Alignment alignment,
        String text,
        float scale,
        int color,
        int backgroundColor
) implements RoadSignElement {

    /**
     * Creates a new TextElement with the given parameters.
     */
    public TextElement(int localX, int localY, Alignment alignment, String text, float scale, Color color, @Nullable Color backgroundColor) {
        this(localX, localY, alignment, text, scale, color.getRGB(), backgroundColor != null ? backgroundColor.getRGB() : 0);
    }

    public TextElement withText(String text) {
        return new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
    }

    public TextElement withColor(Color color) {
        return new TextElement(localX, localY, alignment, text, scale, color.getRGB(), backgroundColor);
    }

    public TextElement withScale(float scale) {
        return new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
    }

    public TextElement withPosition(int localX, int localY) {
        return new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
    }

    public TextElement withAlignment(Alignment alignment) {
        return new TextElement(localX, localY, alignment, text, scale, color, backgroundColor);
    }
}
