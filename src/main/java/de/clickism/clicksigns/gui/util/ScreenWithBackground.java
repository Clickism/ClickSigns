package de.clickism.clicksigns.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * A screen that renders a background
 */
public abstract class ScreenWithBackground extends Screen {
    /**
     * Creates a new screen with background.
     *
     * @param component The title of the screen
     */
    protected ScreenWithBackground(Component component) {
        super(component);
    }

    @Override
    public void render(GuiGraphics graphics, int i, int j, float f) {
        this.renderBackground(graphics);
        super.render(graphics, i, j, f);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.fillGradient(0, 0, this.width, this.height, -0x4FEFEFF0, -0x3FEFEFF0);
    }
}
