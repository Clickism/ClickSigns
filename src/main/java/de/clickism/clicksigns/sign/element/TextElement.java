package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.util.Util;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec2;

import java.util.List;
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
    public static final int MIN_TEXT_PT = 3;
    public static final int MAX_TEXT_PT = 72;

    /**
     * Type key
     */
    public static final String TYPE = "text";

    /**
     * Creates a new text element with default properties.
     *
     * @return the default text element
     */
    public static TextElement createDefault() {
        return new TextElement(0, 0,
            Alignment.CENTER,
            "",
            1.0f,
            TextStyle.DEFAULT
        );
    }

    @Override
    public String typeKey() {
        return TYPE;
    }

    @Override
    public float width() {
        return textSpaceToSignSpace(totalSize().width());
    }

    @Override
    public float height() {
        return textSpaceToSignSpace(totalSize().height());
    }

    /**
     * Returns the lines of text, split by newline characters.
     *
     * @return a list of lines of text
     */
    public List<String> lines() {
        // Split the text by newline characters, preserving empty lines
        return List.of(text.split("\n", -1));
    }

    /**
     * Calculates the X offset (from the text position)
     * for a given line of text based on the text alignment.
     *
     * @param line the line of text to calculate the offset for
     * @return the X offset for the line of text
     */
    public int lineXOffset(String line) {
        var lineWidth = Util.font().width(formattedText(line));
        var totalWidth = unpaddedSize().width();
        return switch (style.textAlignment()) {
            case LEFT -> 0;
            case CENTER -> (totalWidth - lineWidth) / 2;
            case RIGHT -> totalWidth - lineWidth;
        };
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
    public Size totalSize() {
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
        var unpadded = unpaddedSize();
        var width = unpadded.width();
        var height = unpadded.height();
        if (style.isPaddingShown()) {
            width += style.paddingX() * 2;
            height += style.paddingY() * 2;
        }
        return new Size(width, height);
    }

    /**
     * Calculates the unpadded size of the text element in text space.
     *
     * @return the unpadded size of the text element as a Size object
     */
    public Size unpaddedSize() {
        var lines = lines();
        var maxLineWidth = lines.stream()
            .map(this::formattedText)
            .mapToInt(Util.font()::width)
            .max()
            .orElse(0);
        var lineHeight = Util.font().lineHeight;
        var height = lineHeight * lines.size() + (lines.size() - 1) * style.lineGap();
        return new Size(maxLineWidth, height);
    }

    /**
     * Returns a FormattedCharSequence for the given text, applying the text style.
     *
     * @param text the text to format
     * @return a FormattedCharSequence with the applied text style
     */
    public FormattedCharSequence formattedText(String text) {
        return FormattedCharSequence.forward(text, style.asComponentStyle());
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
