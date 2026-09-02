package de.clickism.clicksigns.sign;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

/**
 * Color Reserver that allows defining and resolving colors by name.
 */
public class ColorResolver {
    /**
     * Default color provider with predefined colors.
     */
    private static final ColorResolver DEFAULT = ColorResolver.empty()
        .defineRGB("text_light", 0xD0D0D0)
        .defineRGB("text_dark", 0x292929)
        .defineRGB("white", 0xFFFFFF)
        .defineRGB("black", 0x292929)
        .defineRGB("blue", 0x2739EB)
        .defineRGB("brown", 0x844635)
        .defineRGB("green", 0x009345);

    /**
     * Bright red error color used as fallback
     */
    private static final Color ERROR_COLOR = Color.RED;

    private final @Nullable ColorResolver parent;
    private final Map<String, Color> colors = new HashMap<>();

    /**
     * Creates a new ColorResolver with an optional parent resolver delegate.
     *
     * @param parent the parent ColorResolver to delegate to if a color is not found in this resolver, or null
     */
    protected ColorResolver(@Nullable ColorResolver parent) {
        this.parent = parent;
    }

    /**
     * Resolves a color by name. If the name is not found, it returns a default error color (red).
     *
     * @param name the name of the color to resolve
     * @return the resolved Color, or a default error color if the name is not found
     */
    public Color resolve(@Nullable String name) {
        try {
            return resolveOrThrow(name);
        } catch (IllegalArgumentException e) {
            return ERROR_COLOR;
        }
    }

    /**
     * Resolves a color by name. If the name is not found, it throws an IllegalArgumentException.
     *
     * @param name the name of the color to resolve
     * @return the resolved Color
     * @throws IllegalArgumentException if the name is not found in this resolver or any parent resolver
     */
    public Color resolveOrThrow(@Nullable String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Color name cannot be null.");
        }
        // Try name
        var color = get(name);
        if (color != null) {
            return color;
        }
        // Try hex
        var hexColor = parseHex(name);
        if (hexColor != null) {
            return hexColor;
        }
        throw new IllegalArgumentException("Color '" + name + "' not found in ColorResolver.");
    }

    /**
     * Resolves a color by name. If the name is not found, it returns the provided default color.
     *
     * @param name         the name of the color to resolve
     * @param defaultColor the default color to return if the name is not found
     * @return the resolved Color, or the provided default color if the name is not found
     */
    public Color resolveOrDefault(@Nullable String name, Color defaultColor) {
        try {
            return resolveOrThrow(name);
        } catch (IllegalArgumentException e) {
            return defaultColor;
        }
    }

    /**
     * Resolves a color by name. If the name is not found, it returns null.
     *
     * @param name the name of the color to resolve
     * @return the resolved Color, or null if the name is not found
     */
    public @Nullable Color resolveOrNull(@Nullable String name) {
        return resolveOrDefault(name, null);
    }

    /**
     * Checks if a color with the given name can be resolved.
     *
     * @param name the name of the color to check
     * @return true if the color can be resolved, false otherwise
     */
    public boolean isValidColor(@Nullable String name) {
        try {
            resolveOrThrow(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public String suggestColor(@Nullable String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        var closest = definedColors().stream()
            .filter(name -> name.startsWith(value))
            .min(Comparator.comparingInt(String::length))
            .orElse("");
        if (value.length() >= closest.length()) {
            return "";
        }
        return closest.substring(value.length());
    }

    /**
     * Resolves a color by name and returns its ARGB integer value.
     * If the name is not found, it returns the ARGB value of a default error color (red).
     *
     * @param name the name of the color to resolve
     * @return the RGB integer value of the resolved color, or the RGB value of a default error color if the name is not found
     */
    public int resolveInt(@Nullable String name) {
        return resolve(name).getRGB();
    }

    /**
     * Defines a color with the given name using an RGB integer value. If the name already exists, it will be overwritten.
     *
     * @param name the name of the color
     * @param rgb  the RGB integer value of the color (0xRRGGBB)
     * @return this ColorResolver for chaining
     */
    public ColorResolver defineRGB(String name, int rgb) {
        return this.define(name, new Color(rgb));
    }

    /**
     * Defines a color with the given name using an RGBA integer value. If the name already exists, it will be overwritten.
     *
     * @param name the name of the color
     * @param rgba the RGBA integer value of the color (0xAARRGGBB)
     * @return this ColorResolver for chaining
     */
    public ColorResolver defineRGBA(String name, int rgba) {
        return this.define(name, new Color(rgba, true));
    }

    /**
     * Defines a color with the given name. If the name already exists, it will be overwritten.
     *
     * @param name  the name of the color
     * @param color the color to associate with the name
     * @return this ColorResolver for chaining
     */
    public ColorResolver define(String name, Color color) {
        colors.put(name, color);
        return this;
    }

    /**
     * Checks if a color with the given name can be resolved.
     *
     * @param name the name of the color to check
     * @return true if the color is defined in this or any parent resolver, false otherwise
     */
    public boolean has(String name) {
        return colors.containsKey(name) || (parent != null && parent.has(name));
    }

    /**
     * Returns a set of all color names defined in this resolver.
     *
     * @return a set of all color names defined in this resolver
     */
    public Set<String> definedColors() {
        var colors = new HashSet<>(this.colors.keySet());
        if (parent != null) {
            colors.addAll(parent.definedColors());
        }
        return Collections.unmodifiableSet(colors);
    }

    /**
     * Gets the color associated with the given name from this resolver or any parent resolver.
     *
     * @param name the name of the color to get
     * @return the Color associated with the name, or null if the name couldn't be resolved
     */
    protected @Nullable Color get(String name) {
        if (colors.containsKey(name)) {
            return colors.get(name);
        }
        if (parent != null) {
            return parent.get(name);
        }
        return null;
    }

    @Nullable
    public Color parseHex(String hex) throws IllegalArgumentException {
        if (!hex.startsWith("#")) return null;
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Parse a color and supports hex strings and named colors.
     *
     * @param color the color to parse
     * @return the parsed Color
     * @throws IllegalArgumentException if the color format is unsupported or invalid
     */
    public Color parse(String color) throws IllegalArgumentException {
        // Hex color
        if (color.startsWith("#")) {
            try {
                return Color.decode(color);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid hex color: " + color, e);
            }
        }
        // Named color
        return resolveOrThrow(color);
    }

    /**
     * Parses a color from a string and defines it with the given name.
     * Fails silently if the color string is invalid or cannot be parsed.
     *
     * @param name  the name of the color to define
     * @param color the color string to parse and define, which can be a hex string or a named color.
     */
    public void tryParseAndDefine(String name, String color) {
        try {
            define(name, parse(color));
        } catch (IllegalArgumentException ignored) {
            // Fail silently
        }
    }

    /**
     * Converts a Color to a hex string in the format #AARRGGBB.
     *
     * @param color the color to convert
     * @return the hex string representation of the color
     */
    public static String toHexString(Color color) {
        return String.format("#%02X%02X%02X%02X", color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * Creates a new ColorResolver with the given parent resolver. The parent resolver will be used as a fallback when resolving colors.
     *
     * @param parent the parent ColorResolver to delegate to if a color is not found in the new resolver
     * @return a new ColorResolver with the specified parent resolver
     */
    public static ColorResolver withParent(ColorResolver parent) {
        return new ColorResolver(parent);
    }

    /**
     * Creates a new ColorResolver with the default color provider as its parent.
     * This allows using the predefined colors and adding custom colors on top.
     *
     * @return a new ColorResolver with the default color provider as its parent
     */
    public static ColorResolver withDefault() {
        return new ColorResolver(DEFAULT);
    }

    /**
     * Creates a new ColorResolver with no parent resolver.
     *
     * @return a new ColorResolver with no parent resolver
     */
    public static ColorResolver empty() {
        return new ColorResolver(null);
    }
}
