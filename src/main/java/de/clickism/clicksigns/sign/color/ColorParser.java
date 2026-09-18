package de.clickism.clicksigns.sign.color;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * Parses a {@link de.clickism.clicksigns.sign.color.DynamicColor} in accordance to the format,
 * and using the named colors defined in the given {@link ColorResolver}.
 */
public class ColorParser {
    /**
     * Parse a color and supports hex strings and named colors.
     *
     * @param color the color to parse
     * @return the parsed Color
     * @throws IllegalArgumentException if the color format is unsupported or invalid
     */
    public int parse(String color, ColorResolver colorResolver) throws IllegalArgumentException {
        var parts = color.trim().split(":", 2);
        var mainColor = parseColor(parts[0], colorResolver);
        if (parts.length > 1) {
            // Apply modifiers
            var modifierInstances = parseModifiers(parts[1], colorResolver);
            for (var instance : modifierInstances) {
                mainColor = instance.apply(mainColor);
            }
        }
        return mainColor;
    }

    private int parseColor(String color, ColorResolver colorResolver) throws IllegalArgumentException {
        // Hex color
        if (color.startsWith("#")) {
            var hexColor = parseHex(color);
            if (hexColor == null) {
                throw new IllegalArgumentException("Invalid hex color: " + color);
            }
            return hexColor;
        }
        // Named color, try the resolver
        var named = colorResolver.getNamed(color);
        if (named == null) {
            throw new IllegalArgumentException("Unknown color: " + color);
        }
        return named;
    }

    /**
     * Parses a hex color string and returns the corresponding Color object.
     * Returns null if the string is not a valid hex color.
     *
     * @param hex the hex color string to parse
     * @return the parsed Color object, or null if the string is not a valid hex color
     */
    public @Nullable Integer parseHex(String hex) throws IllegalArgumentException {
        if (!hex.startsWith("#")) return null;
        try {
            return Color.decode(hex).getRGB();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<ColorModifier.Instance> parseModifiers(String modifiers, ColorResolver colorResolver) {
        var modifierStrings = modifiers.split(":");
        return Arrays.stream(modifierStrings)
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> parseModifier(s, colorResolver))
            .toList();
    }

    private ColorModifier.Instance parseModifier(String modifier, ColorResolver colorResolver) {
        var parts = modifier.split("(?<=\\D)(?=\\d)", 2);
        var name = parts[0];
        List<String> args = parts.length > 1
            ? List.of(parts[1])
            : List.of();
        var colorModifier = ColorModifier.ALL.get(name);
        if (colorModifier == null) {
            throw new IllegalArgumentException("Unknown color modifier: " + name);
        }
        return new ColorModifier.Instance(colorModifier, new ColorContext(colorResolver, args));
    }
}
