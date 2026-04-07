package de.clickism.clicksigns.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A widget that can contain other widgets as children.
 */
public abstract class NestedWiget extends AbstractWidget {
    private final List<AbstractWidget> children = new ArrayList<>();

    /**
     * Creates a new nested widget.
     *
     * @param x The x position of the widget.
     * @param y The y position of the widget.
     */
    public NestedWiget(int x, int y) {
        super(x, y, 0, 0, Component.empty());
    }

    /**
     * Adds a child widget to this widget.
     *
     * @param widget The widget to add as a child.
     */
    protected void addChild(AbstractWidget widget) {
        children.add(widget);
        updateSize();
    }

    /**
     * Calculates and updates the size of this widget based on its children.
     */
    private void updateSize() {
        int maxX = 0;
        int maxY = 0;
        for (var child : children) {
            int boundX = child.getX() + child.getWidth();
            int boundY = child.getY() + child.getHeight();
            if (boundX > maxX) {
                maxX = boundX;
            }
            if (boundY > maxY) {
                maxY = boundY;
            }
        }
        this.width = maxX - this.getX();
        this.height = maxY - this.getY();
    }

    /**
     * Gets the children of this widget.
     *
     * @return The children of this widget.
     */
    public List<AbstractWidget> children() {
        return children;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Render all children
        children.forEach(widget -> widget.render(graphics, mouseX, mouseY, delta));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        // Click all children
        for (var child : children) {
            if (!child.isMouseOver(mouseX, mouseY)) continue;
            child.onClick(mouseX, mouseY);
        }
    }
}
