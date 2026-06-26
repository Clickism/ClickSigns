package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.util.Size;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

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
    public ResizeControls(AbstractWidget centerWidget, Consumer<Direction> onResize, Function<Direction, Boolean> canResize) {
        super(centerWidget.getX(), centerWidget.getY());
        this.centerWidget = centerWidget;
        this.onResize = onResize;
        this.canResize = canResize;
        for (Direction direction : Direction.values()) {
            var button = new ResizeButton(direction);
            addChild(button);
        }
        updateSize();
    }

    private Size buttonSize(Direction direction) {
        var thickness = 14;
        return switch (direction) {
            case UP, DOWN -> new Size(centerWidget.getWidth(), thickness);
            case LEFT, RIGHT -> new Size(thickness, centerWidget.getHeight());
        };
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

    private class ResizeButton extends Button {
        private final Direction direction;

        public ResizeButton(Direction direction) {
            super(0, 0, 0, 0, Component.literal(iconOf(direction)),
                    b -> {
                        if (!canResize.apply(direction)) return;
                        onResize.accept(direction);
                    }, DEFAULT_NARRATION);
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

        private void updateSize() {
            var size = buttonSize(direction);
            this.width = size.width();
            this.height = size.height();
        }

        private void updatePosition() {
            var padding = 4;
            var x = centerWidget.getX();
            var y = centerWidget.getY();
            switch (direction) {
                case UP -> {
                    this.setX(x);
                    this.setY(y - this.getHeight() - padding);
                }
                case RIGHT -> {
                    this.setX(x + centerWidget.getWidth() + padding);
                    this.setY(y);
                }
                case DOWN -> {
                    this.setX(x);
                    this.setY(y + centerWidget.getHeight() + padding);
                }
                case LEFT -> {
                    this.setX(x - this.getWidth() - padding);
                    this.setY(y);
                }
            }
        }
    }
}
