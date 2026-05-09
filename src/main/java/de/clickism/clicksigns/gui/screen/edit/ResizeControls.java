package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.util.Size;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Widget for resize controls around a central widget.
 */
public class ResizeControls extends NestedWidget {
    private final Consumer<Direction> onResize;
    private final AbstractWidget centerWidget;

    /**
     * Creates a new resize controls widget
     *
     * @param centerWidget widget to place the controls around
     * @param onResize     resize callback
     */
    public ResizeControls(AbstractWidget centerWidget, Consumer<Direction> onResize) {
        super(centerWidget.getX(), centerWidget.getY());
        this.centerWidget = centerWidget;
        this.onResize = onResize;
        for (Direction direction : Direction.values()) {
            var button = resizeButton(direction);
            positionButton(button, direction);
            addChild(button);
        }
        updateSize();
    }

    private Button resizeButton(Direction direction) {
        var icon = iconOf(direction);
        var size = buttonSize(direction);
        return Button.builder(Component.literal(icon), b -> {
                    onResize.accept(direction);
                })
                .size(size.width(), size.height())
                .build();
    }

    private void positionButton(Button button, Direction direction) {
        var padding = 4;
        var x = centerWidget.getX();
        var y = centerWidget.getY();
        switch (direction) {
            case UP -> {
                button.setX(x);
                button.setY(y - button.getHeight() - padding);
            }
            case RIGHT -> {
                button.setX(x + centerWidget.getWidth() + padding);
                button.setY(y);
            }
            case DOWN -> {
                button.setX(x);
                button.setY(y + centerWidget.getHeight() + padding);
            }
            case LEFT -> {
                button.setX(x - button.getWidth() - padding);
                button.setY(y);
            }
        }
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
}
