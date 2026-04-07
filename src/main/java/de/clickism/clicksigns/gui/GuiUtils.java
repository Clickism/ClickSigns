package de.clickism.clicksigns.gui;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;

/**
 * Utility class for gui logic
 */
public class GuiUtils {
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
}
