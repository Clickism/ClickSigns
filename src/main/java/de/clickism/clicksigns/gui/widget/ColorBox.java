package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.ColorResolver;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

import java.awt.*;
import java.util.Comparator;

/**
 * Edit box for a color value that dynamically updates its text color
 * based on the resolved color from a ColorResolver.
 */
public class ColorBox extends LazyEditBox {
    private final ColorResolver colorResolver;

    public ColorBox(int x, int y, int width, int height, ColorResolver colorResolver) {
        super(GuiUtils.font(), x, y, width, height, Component.empty());
        this.colorResolver = colorResolver;
        this.setResponder(this::onChange);
        this.setFilter(v -> v != null && !v.matches(".*\\s.*")); // Disallow whitespace
    }

    private void onChange(String value) {
        try {
            var color = colorResolver.resolveOrThrow(value);
            this.setTextColor(color.getRGB());
        } catch (IllegalArgumentException e) {
            this.setTextColor(DEFAULT_TEXT_COLOR);
        }
        this.setSuggestion(getSuggestionFor(value));
    }

    private String getSuggestionFor(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        var closest = colorResolver.definedColors().stream()
                .filter(name -> name.startsWith(value))
                .min(Comparator.comparingInt(String::length))
                .orElse("");
        if (value.length() >= closest.length()) {
            return "";
        }
        return closest.substring(value.length());
    }

    public String colorValue() {
        return this.getValue();
    }

    public String colorValueOrNull() {
        var value = this.getValue();
        if (value.isEmpty()) {
            return null;
        }
        return value;
    }
}
