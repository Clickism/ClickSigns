package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.widget.ColoredButton;
import de.clickism.clicksigns.util.Size;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static de.clickism.clicksigns.gui.GuiUtils.INACTIVE_ALPHA;

/**
 * Widget for resize controls around a central widget.
 */
public class ResizeControls extends NestedWidget {
    private final AbstractWidget centerWidget;
    private final Consumer<Direction> onResize;
    private final Function<Direction, Boolean> canResize;

    /**
     * Creates a new resize controls widget
     *
     * @param centerWidget widget to place the controls around
     * @param onResize     resize callback
     */
    // TODO: on the 32x32 when you press down it jumps to 32x16 but up goes to 32x24
    public ResizeControls(AbstractWidget centerWidget, Consumer<Direction> onResize, Function<Direction, Boolean> canResize) {
        super(centerWidget.getX(), centerWidget.getY());
        this.centerWidget = centerWidget;
        this.onResize = onResize;
        this.canResize = canResize;
        for (Direction direction : Direction.values()) {
            var button = new ResizeButton(direction);
            addChild(button);
        }
        updateSizeAndPosition();
    }

    private String iconOf(Direction direction) {
        return switch (direction) {
            case UP -> "↑";
            case RIGHT -> "→";
            case DOWN -> "↓";
            case LEFT -> "←";
        };
    }

    /**
     * The direction of a resize
     */
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private class ResizeButton extends ColoredButton {
        private final Direction direction;

        public ResizeButton(Direction direction) {
            super(0, 0, 0, 0, Color.BLACK, Component.literal(iconOf(direction)),
                    b -> {
                        if (!canResize.apply(direction)) return;
                        onResize.accept(direction);
                    });
            this.direction = direction;
            this.updateSize();
            this.updatePosition();
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
            if (!canResize.apply(direction)) {
                this.setAlpha(INACTIVE_ALPHA);
            } else {
                this.setAlpha(1);
            }
            super.renderWidget(guiGraphics, i, j, f);
        }

        private Size buttonSize(Direction direction) {
            var thickness = 14;
            var width = maxX() - minX();
            var height = maxY() - minY();
            return switch (direction) {
                case UP, DOWN -> new Size(width, thickness);
                case LEFT, RIGHT -> new Size(thickness, height);
            };
        }

        private void updateSize() {
            var size = buttonSize(direction);
            this.width = size.width();
            this.height = size.height();
        }

        private int minX() {
            if (centerWidget instanceof NestedWidget nested) {
                return nested.minX();
            }
            return centerWidget.getX();
        }

        private int minY() {
            if (centerWidget instanceof NestedWidget nested) {
                return nested.minY();
            }
            return centerWidget.getY();
        }

        private int maxX() {
            return centerWidget.getX() + centerWidget.getWidth();
        }

        private int maxY() {
            return centerWidget.getY() + centerWidget.getHeight();
        }

        private void updatePosition() {
            var padding = 4;
            var minX = minX();
            var minY = minY();
            var maxX = maxX();
            var maxY = maxY();
            switch (direction) {
                case UP -> {
                    this.setX(minX);
                    this.setY(minY - this.getHeight() - padding);
                }
                case RIGHT -> {
                    this.setX(maxX + padding);
                    this.setY(minY);
                }
                case DOWN -> {
                    this.setX(minX);
                    this.setY(maxY + padding);
                }
                case LEFT -> {
                    this.setX(minX - this.getWidth() - padding);
                    this.setY(minY);
                }
            }
        }
    }
}
