package de.clickism.clicksigns.sign.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Style for text elements, used for rendering.
 *
 * @param color           the RGBA color of the text
 * @param backgroundColor the RGBA color of the text background, or null for no background
 * @param outlineColor    the RGBA color of the text outline, or null for no outline
 * @param outlinePadding  the padding of the text outline, in pixels
 * @param outlineWidth    the width of the text outline, in pixels
 */
public record TextStyle(
    String color,
    Optional<String> backgroundColor,
    Optional<String> outlineColor,
    int outlinePadding,
    int outlineWidth
) {
    /**
     * Default text style.
     */
    public static final TextStyle DEFAULT = new TextStyle(
        "foreground",
        Optional.empty(),
        Optional.empty(),
        // TODO: Decide on how outline and background is rendered
        2,
        1
    );

    /**
     * Creates a new text style with the given color, background color, outline color, outline padding, and outline width.
     *
     * @param color           the RGBA color of the text
     * @param backgroundColor the RGBA color of the text background, or null for no background
     * @param outlineColor    the RGBA color of the text outline, or null for no outline
     * @param outlinePadding  the padding of the text outline, in pixels
     * @param outlineWidth    the width of the text outline, in pixels
     */
    public TextStyle(
        @NotNull String color,
        @Nullable String backgroundColor,
        @Nullable String outlineColor,
        int outlinePadding,
        int outlineWidth
    ) {
        this(
            color,
            Optional.ofNullable(backgroundColor),
            Optional.ofNullable(outlineColor),
            outlinePadding,
            outlineWidth
        );
    }

    /**
     * Creates a new text style with the given color, keeping the other properties the same.
     *
     * @param newColor the new RGBA color of the text
     * @return a new text style with the updated color
     */
    public TextStyle withColor(@NotNull String newColor) {
        return new TextStyle(newColor, backgroundColor, outlineColor, outlinePadding, outlineWidth);
    }

    /**
     * Creates a new text style with the given background color, keeping the other properties the same.
     *
     * @param newBackgroundColor the new RGBA color of the text background, or null for no background
     * @return a new text style with the updated background color
     */
    public TextStyle withBackgroundColor(@Nullable String newBackgroundColor) {
        return new TextStyle(color, Optional.ofNullable(newBackgroundColor), outlineColor, outlinePadding, outlineWidth);
    }

    /**
     * Creates a new text style with the given outline color, keeping the other properties the same.
     *
     * @param newOutlineColor the new RGBA color of the text outline, or null for no outline
     * @return a new text style with the updated outline color
     */
    public TextStyle withOutlineColor(@Nullable String newOutlineColor) {
        return new TextStyle(color, backgroundColor, Optional.ofNullable(newOutlineColor), outlinePadding, outlineWidth);
    }

    /**
     * Creates a new text style with the given outline padding, keeping the other properties the same.
     *
     * @param newOutlinePadding the new padding of the text outline, in pixels
     * @return a new text style with the updated outline padding
     */
    public TextStyle withOutlinePadding(int newOutlinePadding) {
        return new TextStyle(color, backgroundColor, outlineColor, newOutlinePadding, outlineWidth);
    }

    /**
     * Creates a new text style with the given outline width, keeping the other properties the same.
     *
     * @param newOutlineWidth the new width of the text outline, in pixels
     * @return a new text style with the updated outline width
     */
    public TextStyle withOutlineWidth(int newOutlineWidth) {
        return new TextStyle(color, backgroundColor, outlineColor, outlinePadding, newOutlineWidth);
    }
}
