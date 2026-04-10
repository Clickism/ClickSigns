package de.clickism.clicksigns.sign;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Collection of predefined sign colors.
 */
public class SignColors {
    private static final Map<String, Color> COLORS = new HashMap<>();

    // Predefined colors
    public static final Color WHITE = rgb("white", 0xFFFFFF);
    public static final Color BLACK = rgb("black", 0x292929);
    public static final Color BLUE = rgb("blue", 0x2739EB);
    public static final Color BROWN = rgb("brown", 0x844635);
    public static final Color GREEN = rgb("green", 0x009345);

    private static Color rgb(String key, int color) {
        return register(key, new Color(color));
    }

    private static Color rgba(String key, int color) {
        return register(key, new Color(color, true));
    }

    private static Color register(String key, Color color) {
        COLORS.put(key, color);
        return color;
    }

    /**
     * Gets a color by its key.
     *
     * @param key the key of the color
     * @return the color, or null if no color with the given key exists
     */
    public static @Nullable Color get(String key) {
        return COLORS.get(key);
    }
}
