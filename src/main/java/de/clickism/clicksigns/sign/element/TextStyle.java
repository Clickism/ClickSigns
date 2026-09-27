package de.clickism.clicksigns.sign.element;

import de.clickism.clicksigns.sign.color.DynamicColor;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Style for text elements, used for rendering.
 *
 * @param color           the RGBA color of the text
 * @param backgroundColor the RGBA color of the text background, or null for no background
 * @param outlineColor    the RGBA color of the text outline, or null for no outline
 * @param outlineWidth    the width of the text outline, in text pixels
 * @param paddingX        the horizontal padding of the text, in text pixels
 * @param paddingY        the vertical padding of the text, in text pixels
 */
public record TextStyle(
    String color,
    Optional<String> backgroundColor,
    Optional<String> outlineColor,
    int outlineWidth,
    int paddingX,
    int paddingY,
    TextAlignment textAlignment,
    int lineGap,
    Set<Formatting> formattings
) {
    public static final int MAX_OUTLINE_WIDTH = 10;
    public static final int MAX_PADDING = 20;

    /**
     * Default text style.
     */
    public static final TextStyle DEFAULT = new TextStyle(
        "foreground",
        Optional.empty(),
        Optional.empty(),
        1,
        2,
        1,
        TextAlignment.CENTER,
        0,
        Set.of()
    );

    /**
     * Creates a new text style with the given color, background color, outline color, outline padding, and outline width.
     *
     * @param color           the RGBA color of the text
     * @param backgroundColor the RGBA color of the text background, or null for no background
     * @param outlineColor    the RGBA color of the text outline, or null for no outline
     * @param outlineWidth    the width of the text outline, in text pixels
     * @param paddingX        the horizontal padding of the text, in text pixels
     * @param paddingY        the vertical padding of the text, in text pixels
     */
    public TextStyle(
        @NotNull String color,
        @Nullable String backgroundColor,
        @Nullable String outlineColor,
        int outlineWidth,
        int paddingX,
        int paddingY,
        TextAlignment textAlignment,
        int lineGap,
        Set<Formatting> formattings
    ) {
        this(
            color,
            Optional.ofNullable(backgroundColor),
            Optional.ofNullable(outlineColor),
            outlineWidth,
            paddingX,
            paddingY,
            textAlignment,
            lineGap,
            formattings
        );
    }

    /**
     * Checks if the text style has a background or outline color, indicating that padding should be shown.
     *
     * @return true if the text style has a background or outline color, false otherwise
     */
    public boolean isPaddingShown() {
        return isBackgroundShown() || isOutlineShown();
    }

    /**
     * Checks if the text style's outline is visible
     *
     * @return true if the text style has an outline color and a positive outline width, false otherwise
     */
    public boolean isOutlineShown() {
        return outlineColor.isPresent() && outlineWidth > 0;
    }

    /**
     * Checks if the text style's background is visible
     *
     * @return true if the text style has a background color, false otherwise
     */
    public boolean isBackgroundShown() {
        return backgroundColor.isPresent();
    }

    /**
     * Creates a new text style with the given color, keeping the other properties the same.
     *
     * @param newColor the new RGBA color of the text
     * @return a new text style with the updated color
     */
    public TextStyle withColor(@NotNull @DynamicColor String newColor) {
        return new TextStyle(newColor, backgroundColor, outlineColor, outlineWidth, paddingX, paddingY, textAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given background color, keeping the other properties the same.
     *
     * @param newBackgroundColor the new RGBA color of the text background, or null for no background
     * @return a new text style with the updated background color
     */
    public TextStyle withBackgroundColor(@DynamicColor String newBackgroundColor) {
        newBackgroundColor = newBackgroundColor != null && newBackgroundColor.isEmpty()
            ? null
            : newBackgroundColor;
        return new TextStyle(color, Optional.ofNullable(newBackgroundColor), outlineColor, outlineWidth, paddingX, paddingY, textAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given outline color, keeping the other properties the same.
     *
     * @param newOutlineColor the new RGBA color of the text outline, or null for no outline
     * @return a new text style with the updated outline color
     */
    public TextStyle withOutlineColor(@DynamicColor String newOutlineColor) {
        newOutlineColor = newOutlineColor != null && newOutlineColor.isEmpty()
            ? null
            : newOutlineColor;
        return new TextStyle(color, backgroundColor, Optional.ofNullable(newOutlineColor), outlineWidth, paddingX, paddingY, textAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given outline width, keeping the other properties the same.
     *
     * @param newOutlineWidth the new width of the text outline, in pixels
     * @return a new text style with the updated outline width
     */
    public TextStyle withOutlineWidth(int newOutlineWidth) {
        return new TextStyle(color, backgroundColor, outlineColor, newOutlineWidth, paddingX, paddingY, textAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given horizontal padding, keeping the other properties the same.
     *
     * @param newPaddingX the new horizontal padding of the text, in pixels
     * @return a new text style with the updated horizontal padding
     */
    public TextStyle withPaddingX(int newPaddingX) {
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, newPaddingX, paddingY, textAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given vertical padding, keeping the other properties the same.
     *
     * @param newPaddingY the new vertical padding of the text, in pixels
     * @return a new text style with the updated vertical padding
     */
    public TextStyle withPaddingY(int newPaddingY) {
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, paddingX, newPaddingY, textAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given text alignment, keeping the other properties the same.
     *
     * @param newTextAlignment the new text alignment of the text
     * @return a new text style with the updated text alignment
     */
    public TextStyle withTextAlignment(TextAlignment newTextAlignment) {
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, paddingX, paddingY, newTextAlignment, lineGap, formattings);
    }

    /**
     * Creates a new text style with the given line gap, keeping the other properties the same.
     *
     * @param newLineGap the new line gap of the text, in pixels
     * @return a new text style with the updated line gap
     */
    public TextStyle withLineGap(int newLineGap) {
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, paddingX, paddingY, textAlignment, newLineGap, formattings);
    }

    public TextStyle withFormatting(Set<Formatting> newFormatting) {
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, paddingX, paddingY, textAlignment, lineGap, newFormatting);
    }

    public TextStyle setFormatting(Formatting formatting, boolean enabled) {
        return enabled
            ? withAddedFormatting(formatting)
            : withRemovedFormatting(formatting);
    }

    public TextStyle withAddedFormatting(Formatting formatting) {
        var newFormatting = new HashSet<>(this.formattings);
        newFormatting.add(formatting);
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, paddingX, paddingY, textAlignment, lineGap, newFormatting);
    }

    public TextStyle withRemovedFormatting(Formatting formatting) {
        var newFormatting = new HashSet<>(this.formattings);
        newFormatting.remove(formatting);
        return new TextStyle(color, backgroundColor, outlineColor, outlineWidth, paddingX, paddingY, textAlignment, lineGap, newFormatting);
    }

    public boolean isBold() {
        return formattings.contains(Formatting.BOLD);
    }

    public boolean isItalic() {
        return formattings.contains(Formatting.ITALIC);
    }

    public boolean isUnderline() {
        return formattings.contains(Formatting.UNDERLINE);
    }

    public boolean isStrikethrough() {
        return formattings.contains(Formatting.STRIKETHROUGH);
    }

    public Style asComponentStyle() {
        var style = Style.EMPTY;
        if (isBold()) {
            style = style.withBold(true);
        }
        if (isItalic()) {
            style = style.withItalic(true);
        }
        if (isUnderline()) {
            style = style.withUnderlined(true);
        }
        if (isStrikethrough()) {
            style = style.withStrikethrough(true);
        }
        return style;
    }

    /**
     * Text alignment options for text elements.
     */
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum Formatting {
        BOLD,
        ITALIC,
        UNDERLINE,
        STRIKETHROUGH
    }
}
