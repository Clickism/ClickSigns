package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.elements.Image;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;

public class UiUtil {
    private UiUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the primary color of the given texture as a UiColor.
     *
     * @param texture the texture to get the primary color from
     * @return the primary color of the texture as a UiColor, or default color if no primary color
     */
    public static UiColor primaryColorOf(Texture texture) {
        var primaryColor = texture.primaryColor();
        if (primaryColor != null) {
            return UiColor.rgba(primaryColor);
        }
        return UiColor.BLACK_A50;
    }

    /**
     * Gets the client font
     *
     * @return The client font
     */
    public static Font font() {
        return Minecraft.getInstance().font;
    }

    /**
     * Renders a plus sign on top of other graphics.
     *
     * @param guiGraphics the GuiGraphics to render with
     * @param x           the x position of the center of the plus sign
     * @param y           the y position of the center of the plus sign
     * @param size        how many pixels wide the plus sign should be (minimum 3)
     * @param color       the color of the plus sign
     */
    public static void renderPlusOnTop(GuiGraphics guiGraphics, int x, int y, int size, int color) {
        if (size < 3) {
            size = 3;
        }
        final int finalSize = size;
        final int finalY = y - 1; // Move up by 1 pixel to center the plus sign
        renderWithZ(guiGraphics, 100, () -> {
            guiGraphics.fill(x - finalSize / 2, finalY, x + finalSize / 2 + 1, finalY + 1, color);
            guiGraphics.fill(x, finalY - finalSize / 2, x + 1, finalY + finalSize / 2 + 1, color);
        });
    }

    /**
     * Renders graphics with a specified z-index.
     *
     * @param guiGraphics  the GuiGraphics to render with
     * @param z            the z-index to render at
     * @param renderAction the action to perform for rendering
     */
    public static void renderWithZ(GuiGraphics guiGraphics, int z, Runnable renderAction) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, z);
        renderAction.run();
        guiGraphics.pose().popPose();
    }

    /**
     * Renders an outline around a rectangle.
     *
     * @param graphics     the GuiGraphics to render with
     * @param x            the x position of the top-left corner of the rectangle
     * @param y            the y position of the top-left corner of the rectangle
     * @param width        the width of the rectangle
     * @param height       the height of the rectangle
     * @param outlineWidth the width of the outline
     * @param color        the color of the outline
     */
    public static void renderOutline(
        GuiGraphics graphics,
        int x,
        int y,
        int width,
        int height,
        int outlineWidth,
        int color
    ) {
        // Top
        graphics.fill(x, y, x + width, y + outlineWidth, color);
        // Bottom
        graphics.fill(x, y + height - outlineWidth, x + width, y + height, color);
        // Left
        graphics.fill(x, y + outlineWidth, x + outlineWidth, y + height - outlineWidth, color);
        // Right
        graphics.fill(x + width - outlineWidth, y + outlineWidth, x + width, y + height - outlineWidth, color);
    }

    /**
     * Copies the given text to the system clipboard.
     *
     * @param text the text to copy
     */
    public static void copyToClipboard(String text) {
        var keyboard = Minecraft.getInstance().keyboardHandler;
        keyboard.setClipboard(text);
    }

    /**
     * Creates an Image from a Texture, scaling it by the default texture render scale.
     *
     * @param texture the texture to create an image from
     * @return the created Image
     */
    public static Image imageOf(Texture texture) {
        return imageOf(texture, UI_SCALE);
    }

    /**
     * Creates an Image from a Texture, scaling it by the given scale factor.
     *
     * @param texture the texture to create an image from
     * @param scale   the scale factor to apply to the texture's dimensions
     * @return the created Image
     */
    public static Image imageOf(Texture texture, float scale) {
        return new Image(
            texture.location(),
            (int) (texture.width() * scale),
            (int) (texture.height() * scale)
        );
    }
}
