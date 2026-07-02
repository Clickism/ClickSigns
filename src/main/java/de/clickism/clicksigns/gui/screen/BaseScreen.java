package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.NestedWidget;
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

    private @Nullable FocusData lastFocus;

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

    @Override
    protected void rebuildWidgets() {
        super.rebuildWidgets();
        // Restore focus to the last focused widget if it still exists
        if (lastFocus == null) return;
        var widget = lastFocus.findIn(this.children());
        if (widget != null) {
            this.setFocused(widget);
        } else {
            lastFocus = null; // Widget no longer exists, clear focusAfterRebuild
        }
    }

    @Override
    public void setFocused(@Nullable GuiEventListener guiEventListener) {
        super.setFocused(guiEventListener);
        if (guiEventListener instanceof AbstractWidget widget) {
            // Update last focus
            lastFocus = new FocusData(widget.getX(), widget.getY(), widget);
        }
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

    private record FocusData(int x, int y, AbstractWidget focusedWidget) {
        /**
         * Checks if the given widget matches the focus data.
         *
         * @param widget the widget to check
         * @return true if the widget matches the focus data, false otherwise
         */
        private boolean is(@Nullable AbstractWidget widget) {
            if (widget == null) return false;
            return widget.getX() == x
                   && widget.getY() == y
                   && focusedWidget.getClass() == widget.getClass();
        }

        /**
         * Finds the focused widget in the given list of listeners.
         *
         * @param listeners the list of listeners to search
         * @param <T>       the type of the listeners
         * @return the focused widget if found, null otherwise
         */
        private <T extends GuiEventListener> @Nullable AbstractWidget findIn(List<T> listeners) {
            for (var listener : listeners) {
                if (!(listener instanceof AbstractWidget widget)) continue;
                if (is(widget)) {
                    return widget;
                }
                // Check nested
                if (widget instanceof NestedWidget nested) {
                    var found = findIn(nested.children());
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }
    }
}
