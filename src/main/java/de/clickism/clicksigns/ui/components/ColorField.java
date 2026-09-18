package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.color.ColorModifier;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.elements.input.Field;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

/**
 * A UI element that allows users to input color values.
 */
public class ColorField extends Field<ColorField> {
    private @NotNull UiColor color = UiColor.WHITE;
    private final ColorResolver colorResolver;

    private Consumer<String> onColorChanged = value -> {};

    /**
     * Creates a new ColorField instance with the specified ColorResolver.
     *
     * @param colorResolver the ColorResolver used to validate and suggest colors
     */
    public ColorField(ColorResolver colorResolver) {
        this.colorResolver = colorResolver;
        this.highlightInvalid(true)
            .textShadow(false)
            .suggest(this::suggestColor)
            .validator(string -> {
                if (string.isEmpty()) return true;
                return colorResolver.isValid(string);
            });
        this.updateStyle();
        this.onValueChanged(value -> {
            // Update color
            color = UiColor.rgba(colorResolver.resolveOrDefault(value, color.color()));
            this.updateStyle();
            // Call the external listener
            onColorChanged.accept(value);
        });
    }

    @Override
    public ColorField value(String value) {
        super.value(value);
        this.updateStyle();
        return this;
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

    public ColorField onColorChanged(Consumer<String> listener) {
        this.onColorChanged = listener;
        return this;
    }

    /**
     * Suggests a color based on the current input.
     *
     * @param value the current input value
     * @return a suggested color name, or an empty string if no suggestion is available
     */
    public String suggestColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        var parts = value.split(":", -1);
        var input = parts[parts.length - 1];
        var options = parts.length > 1
            ? ColorModifier.ALL.keySet()
            : colorResolver.definedColors();
        var match = bestMatchIn(input, options);
        return match.isEmpty()
            ? ""
            : match.substring(input.length());
    }

    private String bestMatchIn(String input, Collection<String> options) {
        return options.stream()
            .filter(option -> option.startsWith(input))
            .min(Comparator.comparingInt(String::length))
            .orElse("");
    }
}
