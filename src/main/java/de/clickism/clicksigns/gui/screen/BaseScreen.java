package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.GuiUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * A screen that renders a background
 */
public abstract class BaseScreen extends Screen {
    /**
     * The parent screen to return to when closing this screen, or null if no parent
     */
    protected final @Nullable Screen parent;

    /**
     * Creates a new screen with background.
     *
     * @param parent the parent screen to return to when closing this screen
     */
    protected BaseScreen(@Nullable Screen parent) {
        this(Component.empty(), parent);
    }

    /**
     * Creates a new screen with background.
     *
     * @param title  the title of the screen
     * @param parent the parent screen to return to when closing this screen
     */
    protected BaseScreen(Component title, @Nullable Screen parent) {
        super(title);
        this.parent = parent;
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

    @Override
    public void onClose() {
        GuiUtils.openScreen(parent);
    }
}
