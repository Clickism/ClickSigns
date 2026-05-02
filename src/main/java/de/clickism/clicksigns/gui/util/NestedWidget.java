package de.clickism.clicksigns.gui.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * A widget that can contain other widgets as children.
 */
public abstract class NestedWidget extends AbstractWidget {
    private final List<AbstractWidget> children = new ArrayList<>();

    /**
     * Creates a new nested widget.
     *
     * @param x The x position of the widget.
     * @param y The y position of the widget.
     */
    public NestedWidget(int x, int y) {
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
     * Adds a child widget without updating the size of this widget.
     *
     * @param widget The widget to add as a child.
     */
    protected void addChildNoUpdate(AbstractWidget widget) {
        children.add(widget);
    }

    /**
     * Adds multiple child widgets to this widget.
     *
     * @param widgets The widgets to add as children.
     */
    protected void addChildren(AbstractWidget... widgets) {
        for (var widget : widgets) {
            addChild(widget);
        }
        updateSize();
    }

    /**
     * Adds multiple child widgets to this widget.
     *
     * @param widgets The widgets to add as children.
     */
    protected void addChildren(Collection<? extends AbstractWidget> widgets) {
        for (var widget : widgets) {
            addChild(widget);
        }
        updateSize();
    }

    /**
     * Calculates and updates the size of this widget based on its children.
     */
    public void updateSize() {
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
    public void setX(int i) {
        int deltaX = i - this.getX();
        for (var child : children) {
            child.setX(child.getX() + deltaX);
        }
        super.setX(i);
    }

    @Override
    public void setY(int i) {
        int deltaY = i - this.getY();
        for (var child : children) {
            child.setY(child.getY() + deltaY);
        }
        super.setY(i);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        // Render all children
        children.forEach(widget -> widget.render(graphics, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        // Click all children
        for (var child : children) {
            if (!child.isMouseOver(d, e)) continue;
            if (child.mouseClicked(d, e, i)) {
                // Unfocus all children except the clicked one
                children.forEach(c -> c.setFocused(c == child));
                // Need to set the clicked child as focused, otherwise EditBox doesn't work
                child.setFocused(true);
                return true;
            }
        }
        return super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        // Release all children
        for (var child : children) {
            if (child.mouseReleased(d, e, i)) {
                return true;
            }
        }
        return super.mouseReleased(d, e, i);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> consumer) {
        super.visitWidgets(consumer);
        children.forEach(consumer);
    }

    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        // Drag all children
        for (var child : children) {
            if (child.mouseDragged(d, e, i, f, g)) {
                return true;
            }
        }
        return super.mouseDragged(d, e, i, f, g);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        // Press all children
        for (var child : children) {
            if (child.keyPressed(i, j, k)) {
                return true;
            }
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        // Release all children
        for (var child : children) {
            if (child.keyReleased(i, j, k)) {
                return true;
            }
        }
        return super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        // Type all children
        for (var child : children) {
            if (child.charTyped(c, i)) {
                return true;
            }
        }
        return super.charTyped(c, i);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Narrate all children
        for (var child : children) {
            child.updateNarration(narrationElementOutput);
        }
    }
}
