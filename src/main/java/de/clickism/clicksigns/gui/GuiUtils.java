package de.clickism.clicksigns.gui;

import de.clickism.clicksigns.sign.element.SignElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.awt.*;

import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;

/**
 * Utility class for gui logic
 */
public class GuiUtils {
    /**
     * Color for outlining hovered elements
     */
    public static final int OUTLINE_COLOR = Color.RED.getRGB();

    private GuiUtils() {
        // Utility class
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
     * Opens a screen on the client thread
     *
     * @param screen The screen to open
     */
    public static void openScreen(@Nullable Screen screen) {
        var client = Minecraft.getInstance();
        client.execute(() -> client.setScreen(screen));
    }

    /**
     * Closes the current screen on the client thread
     */
    public static void closeScreen() {
        var client = Minecraft.getInstance();
        client.execute(() -> {
            var screen = currentScreen();
            if (screen != null) {
                screen.onClose();
            }
        });
    }

    /**
     * Gets the current screen
     *
     * @return The current screen
     */
    public static @Nullable Screen currentScreen() {
        return Minecraft.getInstance().screen;
    }

    /**
     * Calculates the GUI position for a road sign element based on the anchor position and alignment.
     *
     * @return The calculated position
     */
    public static Vector2i calculateElementPosition(
            int anchorX, int anchorY,
            SignElement element,
            int width, int height
    ) {
        // Calculate position, by default renders bottom-right aligned
        int x = anchorX;
        int y = anchorY;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        x -= halfWidth; // Move left by half width to render in center
        y -= halfHeight; // Move up by half height to render in center-right
        // Now we can position
        x += element.localX() * TEXTURE_RENDER_SCALE;
        y -= element.localY() * TEXTURE_RENDER_SCALE;
        // Apply alignment offset
        x += (int) (element.alignment().offset().x * halfWidth);
        y -= (int) (element.alignment().offset().y * halfHeight);
        // Return position as vector
        return new Vector2i(x, y);
    }

    /**
     * Renders an outline around a rectangle.
     *
     * @param guiGraphics the GuiGraphics to render with
     * @param x           the x position of the rectangle
     * @param y           the y position of the rectangle
     * @param width       the width of the rectangle
     * @param height      the height of the rectangle
     */
    public static void renderOutline(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        renderOutline(guiGraphics, x, y, width, height, OUTLINE_COLOR);
    }

    /**
     * Renders an outline around a rectangle with a custom color.
     *
     * @param guiGraphics  the GuiGraphics to render with
     * @param x            the x position of the rectangle
     * @param y            the y position of the rectangle
     * @param width        the width of the rectangle
     * @param height       the height of the rectangle
     * @param outlineColor the color of the outline
     */
    public static void renderOutline(GuiGraphics guiGraphics, int x, int y, int width, int height, int outlineColor) {
        guiGraphics.renderOutline(x, y, width, height, outlineColor);
    }

    /**
     * Renders an outline around a rectangle with a custom color.
     * Renders on top of other graphics.
     *
     * @param guiGraphics  the GuiGraphics to render with
     * @param x            the x position of the rectangle
     * @param y            the y position of the rectangle
     * @param width        the width of the rectangle
     * @param height       the height of the rectangle
     * @param outlineColor the color of the outline
     */
    public static void renderOutlineOnTop(GuiGraphics guiGraphics, int x, int y, int width, int height, int outlineColor) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 100);
        renderOutline(guiGraphics, x, y, width, height, outlineColor);
        guiGraphics.pose().popPose();
    }
}
