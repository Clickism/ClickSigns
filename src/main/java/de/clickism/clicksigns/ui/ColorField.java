package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.elements.input.Field;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * A UI element that allows users to input color values.
 */
public class ColorField extends Field<ColorField> {
    private @NotNull UiColor color = UiColor.WHITE;

    private Consumer<String> onColorChanged = value -> {};

    /**
     * Creates a new ColorField instance with the specified ColorResolver.
     *
     * @param colorResolver the ColorResolver used to validate and suggest colors
     */
    public ColorField(ColorResolver colorResolver) {
        this.highlightInvalid(true)
            .textShadow(false)
            .suggest(colorResolver::suggestColor)
            .validator(string -> {
                if (string.isEmpty()) return true;
                return colorResolver.isValidColor(string);
            });
        this.updateStyle();
        super.onValueChanged(value -> {
            // Update color
            color = UiColor.of(colorResolver.resolveOrDefault(value, color.toColor()));
            this.updateStyle();
            // Call the external listener
            onColorChanged.accept(value);
        });
    }

    /**
     * Updates the style of the ColorField based on the current color.
     */
    private void updateStyle() {
        this.style(style()
            .textColor(color)
            .backgroundColor(
                color.pickBetterContrasting(UiColor.BLACK, UiColor.WHITE)
            ));
    }

    @Override
    public ColorField onValueChanged(Consumer<String> listener) {
        this.onColorChanged = listener;
        return this;
    }
}
