package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.ui.UiUtil;
import net.minecraft.util.Mth;

import java.util.function.Function;

import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Text element on a road sign.
 *
 * @param x         local X coordinate
 * @param y         local Y coordinate
 * @param alignment alignment of the text
 * @param text      text to display
 * @param scale     scale of the text, where 1.0 is the default size
 * @param style     style of the text
 */
public record TextElement(
    int x,
    int y,
    Alignment alignment,
    String text,
    float scale,
    TextStyle style
) implements SignElement {
    /**
     * Type key
     */
    public static final String TYPE = "text";

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public int signWidth() {
        return calculateSignDimension(UiUtil.font().width(text));
    }

    @Override
    public int signHeight() {
        return calculateSignDimension(UiUtil.font().lineHeight);
    }

    // TODO: Fix text not aligned properly? (I think only in UI)
    private int calculateSignDimension(int dimension) {
        return Mth.ceil(
            dimension // Dimension of text in blocks
            * BLOCK_PIXELS // Convert to pixels
            * TEXT_RENDER_SCALE // Apply render scale
            * this.scale() // Apply scale
        );
    }

    /**
     * Creates a new text element with the given text, keeping the other properties the same.
     *
     * @param text text to display
     * @return a new text element with the given text, keeping the other properties the same
     */
    public TextElement withText(String text) {
        return new TextElement(x, y, alignment, text, scale, style);
    }

    /**
     * Creates a new text element with the given scale, keeping the other properties the same.
     *
     * @param scale scale of the text, where 1.0 is the default size
     * @return a new text element with the given scale, keeping the other properties the same
     */
    public TextElement withScale(float scale) {
        return new TextElement(x, y, alignment, text, scale, style);
    }

    /**
     * Creates a new text element with the given position, keeping the other properties the same.
     *
     * @param localX local X coordinate
     * @param localY local Y coordinate
     * @return a new text element with the given position, keeping the other properties the same
     */
    @Override
    public TextElement withPosition(int localX, int localY) {
        return new TextElement(localX, localY, alignment, text, scale, style);
    }

    /**
     * Creates a new text element with the given alignment, keeping the other properties the same.
     *
     * @param alignment alignment of the text
     * @return a new text element with the given alignment, keeping the other properties the same
     */
    @Override
    public TextElement withAlignment(Alignment alignment) {
        return new TextElement(x, y, alignment, text, scale, style);
    }

    /**
     * Creates a new text element with the given style, keeping the other properties the same.
     *
     * @param style style of the text
     * @return a new text element with the given style, keeping the other properties the same
     */
    public TextElement withStyle(TextStyle style) {
        return new TextElement(x, y, alignment, text, scale, style);
    }

    /**
     * Creates a new text element with the given style updater, keeping the other properties the same.
     *
     * @param styleUpdater function to update the style of the text
     * @return a new text element with the updated style, keeping the other properties the same
     */
    public TextElement withStyle(Function<TextStyle, TextStyle> styleUpdater) {
        return withStyle(styleUpdater.apply(style));
    }
}
