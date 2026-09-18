package de.clickism.clicksigns.sign.color;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Color resolvers are a chain of color definitions that can be used to
 * resolve color names, hex codes, etc. that are defined in {@link DynamicColor}.
 * <p>
 * Color resolvers can be chained together, allowing for a hierarchy of color definitions.
 * When resolving a color, the resolver will first check its own definitions, and if the color is not found,
 * it will delegate to its parent resolver (if any).
 * <p>
 * All color values are in the ARGB format.
 */
public class ColorResolver {
    /**
     * Bright red error color used as fallback
     */
    private static final Integer ERROR_COLOR = 0xFF0000;

    private static final ColorParser COLOR_PARSER = new ColorParser();

    private final @Nullable ColorResolver parent;
    private final Map<String, Integer> colors = new HashMap<>();

    /**
     * Creates a new ColorResolver with an optional parent resolver delegate.
     *
     * @param parent the parent ColorResolver to delegate to if a color is not found in this resolver, or null
     */
    protected ColorResolver(@Nullable ColorResolver parent) {
        this.parent = parent;
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
        return new ColorResolver(DynamicColor.DEFAULT_COLOR_RESOLVER);
    }

    /**
     * Creates a new ColorResolver with no parent resolver.
     *
     * @return a new ColorResolver with no parent resolver
     */
    public static ColorResolver empty() {
        return new ColorResolver(null);
    }

    /**
     * Resolves the given dynamic color to an ARGB integer value.
     * If the color cannot be resolved, it returns a bright red error color (0xFF0000).
     *
     * @param color the dynamic color to resolve
     * @return the resolved ARGB integer value of the color, or 0xFF0000 if the color cannot be resolved
     */
    public int resolve(@DynamicColor String color) {
        try {
            return resolveOrThrow(color);
        } catch (IllegalArgumentException e) {
            return ERROR_COLOR;
        }
    }

    /**
     * Resolves the given dynamic color to an ARGB integer value.
     * If the color cannot be resolved, it throws an IllegalArgumentException.
     *
     * @param name the dynamic color to resolve
     * @return the resolved ARGB integer value of the color
     * @throws IllegalArgumentException if the color cannot be resolved
     */
    public int resolveOrThrow(@DynamicColor String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Color name cannot be null.");
        }
        return COLOR_PARSER.parse(name, this);
    }

    /**
     * Resolves the given dynamic color to an ARGB integer value.
     * If the color cannot be resolved, it returns the specified default color.
     *
     * @param name         the dynamic color to resolve
     * @param defaultColor the default ARGB integer value to return if the color cannot be resolved
     * @return the resolved ARGB integer value of the color, or the defaultColor if the color cannot be resolved
     */
    public int resolveOrDefault(@DynamicColor String name, int defaultColor) {
        try {
            return resolveOrThrow(name);
        } catch (IllegalArgumentException e) {
            return defaultColor;
        }
    }

    /**
     * Checks if a given dynamic color is valid and can be resolved by this ColorResolver.
     *
     * @param color the name of the color to check
     * @return true if the color can be resolved, false otherwise
     */
    public boolean isValid(@DynamicColor String color) {
        try {
            resolveOrThrow(color);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Defines a new color with the given name and RGB value (without alpha).
     * The alpha channel is set to 255 (fully opaque) by default.
     *
     * @param name the name of the color to define
     * @param rgb  the RGB value of the color (without alpha)
     * @return this ColorResolver instance for method chaining
     */
    public ColorResolver define(String name, int rgb) {
        return defineWithAlpha(name, (rgb & 0xFFFFFF) | 0xFF000000);
    }

    /**
     * Defines a new color with the given name and ARGB value.
     *
     * @param name the name of the color to define
     * @param rgba the ARGB value of the color
     * @return this ColorResolver instance for method chaining
     */
    public ColorResolver defineWithAlpha(String name, int rgba) {
        if (name.contains(":")) {
            throw new IllegalArgumentException("Color name cannot contain ':' character: " + name);
        }
        colors.put(name, rgba);
        return this;
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
     * Checks if a color with the given name can be resolved.
     *
     * @param name the name of the color to check
     * @return true if the color is defined in this or any parent resolver, false otherwise
     */
    public boolean hasNamed(String name) {
        return colors.containsKey(name) || (parent != null && parent.hasNamed(name));
    }

    /**
     * Returns the ARGB value of a color with the given name,
     * or null if the color is not defined in this resolver or any parent resolver.
     *
     * @param name the name of the color to retrieve
     * @return the ARGB value of the color, or null if not defined
     */
    @Nullable Integer getNamed(String name) {
        if (colors.containsKey(name)) {
            return colors.get(name);
        }
        if (parent != null) {
            return parent.getNamed(name);
        }
        return null;
    }

    /**
     * Tries to parse the given color string and define it with the specified name.
     * If parsing fails, it fails silently without throwing an exception.
     *
     * @param name  the name of the color to define
     * @param color the color string to parse and define
     */
    public void tryParseAndDefine(String name, @DynamicColor String color) {
        try {
            define(name, COLOR_PARSER.parse(color, this));
        } catch (IllegalArgumentException ignored) {
            // Fail silently
        }
    }
}
