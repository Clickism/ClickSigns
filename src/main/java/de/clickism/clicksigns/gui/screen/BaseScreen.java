package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A screen that renders a background
 */
public abstract class BaseScreen extends Screen {
    /**
     * The parent screen to return to when closing this screen, or null if no parent
     */
    protected final @Nullable Screen parent;
    protected @Nullable GuiEventListener hoveredWidget;
    /**
     * Whether to support colliding widgets.
     * If enabled, tooltips, hovers and clicks will only be sent to the topmost widget under the mouse.
     */
    protected boolean supportCollidingWidgets = false;

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
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        hoveredWidget = GuiUtils.findFirstHoveredWidget(this, mouseX, mouseY);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        graphics.fillGradient(0, 0, this.width, this.height, -0x4FEFEFF0, -0x3FEFEFF0);
    }

    @Override
    public void onClose() {
        GuiUtils.openScreen(parent);
    }

    /**
     * Gets the half width of the screen, used for centering widgets.
     *
     * @return the half width of the screen
     */
    public int halfWidth() {
        return width / 2;
    }

    /**
     * Gets the half height of the screen, used for centering widgets.
     *
     * @return the half height of the screen
     */
    public int halfHeight() {
        return height / 2;
    }

    /**
     * Checks if the given widget is currently hovered by the mouse.
     *
     * @param widget the widget to check
     * @return true if the widget is hovered, false otherwise
     * @throws IllegalStateException if the current screen is not a BaseScreen
     */
    public static boolean isHovered(GuiEventListener widget, int mouseX, int mouseY) {
        var screen = GuiUtils.currentScreen();
        if (!(screen instanceof BaseScreen baseScreen)) {
            throw new IllegalStateException("Current screen is not a BaseScreen, cannot check hovered widget.");
        }
        if (!baseScreen.supportCollidingWidgets) {
            return widget.isMouseOver(mouseX, mouseY);
        }
        return widget.equals(baseScreen.hoveredWidget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the hovered widget is clicked first
        if (hoveredWidget != null && hoveredWidget.mouseClicked(mouseX, mouseY, button)) {
            this.setFocused(hoveredWidget);
            hoveredWidget.setFocused(true);
            if (GuiUtils.isLeftClick(button)) {
                this.setDragging(true);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void setTooltipForNextRenderPass(List<FormattedCharSequence> list, ClientTooltipPositioner clientTooltipPositioner, boolean bl) {
        // Only allow setting if element is hovered
        if (!(hoveredWidget instanceof AbstractWidget abstractWidget)) {
            super.setTooltipForNextRenderPass(list, clientTooltipPositioner, bl);
            return;
        }
        var tooltip = abstractWidget.getTooltip();
        if (tooltip == null) {
            super.setTooltipForNextRenderPass(list, clientTooltipPositioner, bl);
            return;
        }
        List<FormattedCharSequence> hoveredList = abstractWidget.getTooltip().toCharSequence(Minecraft.getInstance());
        if (hoveredList.equals(list)) {
            super.setTooltipForNextRenderPass(list, clientTooltipPositioner, bl);
        }
    }
}
