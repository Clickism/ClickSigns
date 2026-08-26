package de.clickism.clicksigns.gui;

import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clickui.elements.Image;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.awt.*;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * Utility class for gui logic
 */
public class GuiUtils {
    /**
     * Color for outlining hovered elements
     */
    public static final int OUTLINE_COLOR = Color.RED.getRGB();
    /**
     * Color for outlining selected elements
     */
    public static final int SELECTED_OUTLINE_COLOR = Color.GREEN.getRGB();
    /**
     * Color for outlining elements being dragged
     */
    public static final int DRAGGING_OUTLINE_COLOR = Color.MAGENTA.getRGB();
    /**
     * Color for uneditable elements
     */
    public static final int UNEDITABLE_COLOR = 0xFFFF5555;

    /**
     * Alpha value for inactive elements
     */
    public static final float INACTIVE_ALPHA = .3f;

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
        x += element.localX() * DEFAULT_TEXTURE_RENDER_SCALE;
        y -= element.localY() * DEFAULT_TEXTURE_RENDER_SCALE;
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
        renderWithZ(guiGraphics, 100, () -> renderOutline(guiGraphics, x, y, width, height, outlineColor));
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
     * Checks if the given mouse button is a left click.
     *
     * @param button the mouse button to check
     * @return true if the button is a left click, false otherwise
     */
    public static boolean isLeftClick(int button) {
        return button == 0;
    }

    /**
     * Checks if the given mouse button is a right click.
     *
     * @param button the mouse button to check
     * @return true if the button is a right click, false otherwise
     */
    public static boolean isRightClick(int button) {
        return button == 1;
    }

    /**
     * Finds the first hovered widget in the children of this screen.
     *
     * @param mouseX mouse x position
     * @param mouseY mouse y position
     * @return the first hovered widget, or null if none are hovered
     */
    public static @Nullable GuiEventListener findFirstHoveredWidget(Screen screen, int mouseX, int mouseY) {
        for (int i = screen.children().size() - 1; i >= 0; i--) {
            var child = screen.children().get(i);
            if (!child.isMouseOver(mouseX, mouseY)) continue;
            if (!(child instanceof NestedWidget nested)) return child;
            // Iterate over children and find first hovered
            for (int j = nested.children().size() - 1; j >= 0; j--) {
                var nestedChild = nested.children().get(j);
                if (nestedChild.isMouseOver(mouseX, mouseY)) {
                    return nestedChild;
                }
            }
        }
        return null;
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
        return new Image(
            texture.location(),
            texture.width() * DEFAULT_TEXTURE_RENDER_SCALE,
            texture.height() * DEFAULT_TEXTURE_RENDER_SCALE
        );
    }

    /**
     * Adds an alpha value to a color represented as an integer.
     *
     * @param color the color as an integer (ARGB format)
     * @param alpha the alpha value to add (0.0 to 1.0)
     * @return the color with the new alpha value as an integer (ARGB format)
     */
    public static int colorWithAlpha(int color, float alpha) {
        int a = (int) (alpha * 255);
        return (a << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Multiplies the alpha value of a color represented as an integer by a factor.
     *
     * @param color  the color as an integer (ARGB format)
     * @param factor the factor to multiply the alpha value by (0.0 to 1.0)
     * @return the color with the multiplied alpha value as an integer (ARGB format)
     */
    public static int colorWithMultipliedAlpha(int color, float factor) {
        int alpha = (color >> 24) & 0xFF;
        int newAlpha = (int) (alpha * factor);
        return (newAlpha << 24) | (color & 0x00FFFFFF);
    }
}
