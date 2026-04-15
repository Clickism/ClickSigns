package de.clickism.clicksigns.sign.template;

import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ColorProvider {
    /**
     * Default color provider with predefined colors.
     */
    public static final ColorProvider DEFAULT = new ColorProvider()
            .putRgb("text_light", 0xD0D0D0)
            .putRgb("text_dark", 0x292929)
            .putRgb("white", 0xFFFFFF)
            .putRgb("black", 0x292929)
            .putRgb("blue", 0x2739EB)
            .putRgb("brown", 0x844635)
            .putRgb("green", 0x009345);

    private static final int ERROR_COLOR = Color.RED.getRGB();

    private final @Nullable ColorProvider parent;
    private final Map<String, Integer> colors;

    public ColorProvider(@Nullable ColorProvider parent, Map<String, Integer> colors) {
        this.parent = parent;
        this.colors = colors;
    }

    protected ColorProvider() {
        this(null, new HashMap<>());
    }

    public int color(String name) {
        var color = get(name);
        if (color != null) {
            return color;
        }
        return ERROR_COLOR;
    }

    public ColorProvider putRgb(String name, int rgb) {
        colors.put(name, rgb | 0xFF000000);
        return this;
    }

    public ColorProvider putRgba(String name, int rgba) {
        colors.put(name, rgba);
        return this;
    }

    protected @Nullable Integer get(String name) {
        if (colors.containsKey(name)) {
            return colors.get(name);
        }
        if (parent != null) {
            return parent.get(name);
        }
        return DEFAULT.get(name);
    }
}
