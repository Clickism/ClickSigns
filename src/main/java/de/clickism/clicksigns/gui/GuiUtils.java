package de.clickism.clicksigns.gui;

import de.clickism.clicksigns.sign.element.RoadSignElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
    public static void openScreen(Screen screen) {
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
     * Pushes a screen onto the screen stack and opens it
     *
     * @param screen The screen to open
     */
    public static void pushScreen(Screen screen) {
        ScreenStack.INSTANCE.open(screen);
    }

    /**
     * Pops the current screen from the screen stack and opens the previous one
     */
    public static void popScreen() {
        ScreenStack.INSTANCE.back();
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
            RoadSignElement element,
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
}
