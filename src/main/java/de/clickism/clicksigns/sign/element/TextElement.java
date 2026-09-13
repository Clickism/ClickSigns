package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.util.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.function.Function;

import static de.clickism.clicksigns.render.element.TextRenderer.TEXT_RENDER_SCALE;
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
    public float width() {
        return textSpaceToSignSpace(textSize().width());
    }

    @Override
    public float height() {
        return textSpaceToSignSpace(textSize().height());
    }

    /**
     * Calculates the dimension of the sign element in pixels.
     *
     * @param dimension dimension of the text in blocks
     * @param padding   padding of the text in pixels, in the given dimension
     * @return the dimension of the sign element in pixels
     */
    // TODO: Fix text not aligned properly? (I think only in UI)
    private int calculateSignDimension(int dimension, int padding) {
        int result = dimension;
        // If there is a background color, add padding to the dimension
        if (style.isPaddingShown()) {
            result += padding * 2;
            if (style.isOutlineShown()) {
                result += style.outlineWidth() * 2;
            }
        }
        return Mth.ceil(textSpaceToSignSpace(result));
    }

    /**
     * Converts a dimension in text space to sign space, taking into account
     * the block pixels, text render scale, and element scale.
     *
     * @param textSpace dimension in text space
     * @return dimension in sign space
     */
    private float textSpaceToSignSpace(int textSpace) {
        return textSpace * BLOCK_PIXELS * TEXT_RENDER_SCALE * scale;
    }

    /**
     * Returns the total size of the text element, including background
     * and padding, in text space.
     *
     * @return the total size of the text element as a Size object
     */
    public Size textSize() {
        var padded = paddedSize();
        return new Size(
            padded.width() + backgroundOffset() * 2,
            padded.height() + backgroundOffset() * 2
        );
    }

    /**
     * Calculates the offset of the text within the text element, taking into account padding and outline in text space.
     *
     * @return the position of the text as a Vec2
     */
    public Vec2 textOffset() {
        float offsetX = 0;
        float offsetY = -1; // Offset by -1 to center visually
        if (style.isPaddingShown()) {
            offsetX += style.paddingX();
            offsetY += style.paddingY();
            if (style.isOutlineShown()) {
                offsetX += style.outlineWidth();
                offsetY += style.outlineWidth();
            }
        }
        return new Vec2(offsetX, offsetY);
    }

    /**
     * Calculates the padded size of the text element in text space.
     *
     * @return the padded size of the text element as a Size object
     */
    public Size paddedSize() {
        int width = Util.font().width(text);
        int height = Util.font().lineHeight;
        if (style.isPaddingShown()) {
            width += style.paddingX() * 2;
            height += style.paddingY() * 2;
        }
        return new Size(width, height);
    }

    /**
     * Calculates the offset of the background of the text element in text space,
     * taking into account whether the outline is shown.
     *
     * @return the offset of the background as an integer
     */
    public int backgroundOffset() {
        return style.isOutlineShown()
            ? style.outlineWidth()
            : 0;
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
     * @param x local X coordinate
     * @param y local Y coordinate
     * @return a new text element with the given position, keeping the other properties the same
     */
    @Override
    public TextElement withPosition(int x, int y) {
        return new TextElement(x, y, alignment, text, scale, style);
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
