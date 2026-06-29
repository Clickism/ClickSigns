package de.clickism.clicksigns.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple vertical scroll container.
 */
public class VerticalScrollContainer extends AbstractWidget {
    private static final double SCROLL_RATE = 9;
    protected static final int SCROLLBAR_WIDTH = 6;

    private static final int SCROLLBAR_BACKGROUND = -16777216;
    public static final int SCROLLBAR_COLOR = -8355712;
    public static final int SCROLLBAR_SHADOW_COLOR = -4144960;
    public static final int MIN_SCROLLBAR_HEIGHT = 32;

    private double scrollAmount = 0;
    private boolean scrolling;
    private boolean dirtyLayout = true;

    private final List<AbstractWidget> children = new ArrayList<>();
    private final LinearLayout layout = LinearLayout.vertical();

    /**
     * Creates a new vertical scroll container.
     *
     * @param x      the x position of the container
     * @param y      the y position of the container
     * @param width  the width of the container
     * @param height the height of the container
     */
    public VerticalScrollContainer(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    /**
     * Adds a child widget to the container.
     *
     * @param widget the widget to add
     */
    public void addChild(AbstractWidget widget) {
        this.children.add(widget);
        this.layout.add(widget);
        this.dirtyLayout = true;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        recalculatePositionsIfNeeded();
        // Enable scissor to only render children within the container
        guiGraphics.enableScissor(this.getX() - 1, this.getY() - 1, this.getX() + this.width, this.getY() + this.height);
        // Render children
        for (AbstractWidget child : children) {
            child.render(guiGraphics, mouseX, mouseY, tickDelta);
        }
        // Render scrollbar
        if (isScrollbarVisible()) {
            renderScrollbar(guiGraphics);
        }
        // Disable scissor
        guiGraphics.disableScissor();
    }

    /**
     * Checks if the scrollbar should be visible based on the content height and container height.
     *
     * @return true if the scrollbar should be visible, false otherwise
     */
    protected boolean isScrollbarVisible() {
        return maxScrollAmount() > 0;
    }

    /**
     * Calculates the width of the scrollbar, which is SCROLLBAR_WIDTH or 0 if the scrollbar is not visible.
     *
     * @return the width of the scrollbar
     */
    protected int scrollbarWidth() {
        return isScrollbarVisible() ? SCROLLBAR_WIDTH : 0;
    }

    /**
     * Renders the scrollbar on the right side of the container.
     * Mostly inspired by {@link net.minecraft.client.gui.components.AbstractSelectionList#render}
     *
     * @param guiGraphics the graphics context to render with
     */
    private void renderScrollbar(GuiGraphics guiGraphics) {
        int scrollbarX = scrollbarX();
        int scrollbarEndX = scrollbarX + SCROLLBAR_WIDTH;
        // Scrollbar background
        guiGraphics.fill(scrollbarX, this.getY(), scrollbarEndX, this.getY() + this.getHeight(), SCROLLBAR_BACKGROUND);
        // Render scrollbar
        int scrollbarHeight = scrollbarHeight();
        int scrollbarY = (int) (scrollAmount * (height - scrollbarHeight) / maxScrollAmount()) + this.getY();
        int scrollbarEndY = scrollbarY + scrollbarHeight;
        // Render scrollbar
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarEndX, scrollbarEndY, SCROLLBAR_COLOR);
        // Render shadow
        guiGraphics.fill(scrollbarX, scrollbarEndY, scrollbarEndX - 1, scrollbarEndY - 1, SCROLLBAR_SHADOW_COLOR);
    }

    //? if < 1.20.4
    /*@Override*/
    public boolean mouseScrolled(double d, double e, double f) {
        if (!this.visible) return false;
        this.scroll(this.scrollAmount - f * SCROLL_RATE);
        return true;
    }

    /**
     * Set the scroll amount and clamp it.
     *
     * @param amount the new scroll amount
     */
    private void scroll(double amount) {
        this.scrollAmount = Mth.clamp(amount, 0, maxScrollAmount());
        dirtyLayout = true;
    }

    /**
     * Recalculates the positions of the child widgets based on the current scroll amount.
     */
    private void recalculatePositionsIfNeeded() {
        if (!dirtyLayout) return;
        layout.layout(this.getX(), this.getY() - (int) scrollAmount);
        dirtyLayout = false;
    }

    /**
     * Calculates the maximum scroll amount based on the content height and container height.
     *
     * @return the maximum scroll amount
     */
    private int maxScrollAmount() {
        return Math.max(0, contentHeight() - this.height);
    }

    /**
     * Calculates the x position of the scrollbar.
     */
    protected int scrollbarX() {
        return this.getX() + this.width - SCROLLBAR_WIDTH;
    }

    /**
     * Calculates the height of the scrollbar based on the ratio of the container height to the content height.
     *
     * @return the height of the scrollbar
     */
    private int scrollbarHeight() {
        int scrollbarHeight = ((height * height) / contentHeight());
        // Ensure scrollbar is at least 32 pixels high
        int maxScrollbarHeight = height - 8; // Ensure a bit of leeway
        return Mth.clamp(scrollbarHeight, MIN_SCROLLBAR_HEIGHT, maxScrollbarHeight);
    }

    /**
     * Calculates the total height of the content based on the layout.
     *
     * @return the total height of the content
     */
    private int contentHeight() {
        return layout.height();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse) {
        if (!this.visible) return false;
        // Check if mouse is on scrollbar
        if (isMouseOnScrollbar(mouseX, mouseY) && mouse == 0) {
            this.scrolling = true;
            return true;
        }
        // Pass to children
        for (AbstractWidget child : children) {
            if (child.mouseClicked(mouseX, mouseY, mouse)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the mouse is currently hovering over the scrollbar.
     *
     * @param mouseX the x position of the mouse
     * @param mouseY the y position of the mouse
     * @return true if the mouse is hovering over the scrollbar, false otherwise
     */
    private boolean isMouseOnScrollbar(double mouseX, double mouseY) {
        int scrollbarX = scrollbarX();
        int scrollbarEndX = scrollbarX + SCROLLBAR_WIDTH;
        return mouseX >= scrollbarX && mouseX <= scrollbarEndX && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
    }

    /**
     * Stop scrolling when mouse is released
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouse) {
        if (mouse == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, mouse);
    }

    /**
     * Scroll based on mouse drag
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!this.visible || !this.isFocused() || !this.scrolling) return false;
        // Scroll
        if (mouseY < this.getY()) {
            scroll(0); // Scroll to top
        } else if (mouseY > this.getY() + this.height) {
            scroll(maxScrollAmount()); // Scroll to bottom
        } else {
            // Scroll proportionally to mouse position
            double scrollRatio = Math.max(1, maxScrollAmount() / (height - scrollbarHeight()));
            this.scroll(scrollAmount + deltaY * scrollRatio);
        }
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // No narration
    }
}
