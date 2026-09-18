package de.clickism.clicksigns.sign.color;

import net.minecraft.util.FastColor;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.util.FastColor.ABGR32.*;

public interface ColorModifier {
    Map<String, ColorModifier> ALL = new HashMap<>();

    ColorModifier ALPHA = register(create("a", (color, context) -> {
        if (context.arguments().isEmpty()) {
            return color;
        }
        var percentage = context.percentageAt(0);
        if (percentage < 0.1f) {
            throw new IllegalArgumentException("Alpha percentage must be at least 10%");
        }
        var alpha = (int) (percentage * 255);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }));

    ColorModifier DARKEN = register(create("d", (color, context) -> {
        var percentage = context.percentageAt(0);
        var r = red(color);
        var g = green(color);
        var b = blue(color);
        var a = alpha(color);
        r = (int) (r * (1 - percentage));
        g = (int) (g * (1 - percentage));
        b = (int) (b * (1 - percentage));
        return FastColor.ARGB32.color(a, r, g, b);
    }));

    ColorModifier LIGHTEN = register(create("l", (color, context) -> {
        var percentage = context.percentageAt(0);
        var r = red(color);
        var g = green(color);
        var b = blue(color);
        var a = alpha(color);
        r = (int) (r + (255 - r) * percentage);
        g = (int) (g + (255 - g) * percentage);
        b = (int) (b + (255 - b) * percentage);
        return FastColor.ARGB32.color(a, r, g, b);
    }));

    /**
     * The name of the modifier
     */
    String name();

    /**
     * The function that applies the modifier to a color
     */
    ModifierFunction function();

    /**
     * Creates a new ColorModifier with a single argument
     *
     * @param name     the name of the modifier
     * @param function the function that applies the modifier to a color
     * @return a new ColorModifier
     */
    static ColorModifier create(String name, ModifierFunction function) {
        return new ColorModifier() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public ModifierFunction function() {
                return function;
            }
        };
    }

    /**
     * Registers a ColorModifier.
     *
     * @param modifier the ColorModifier to register
     * @return the registered ColorModifier
     */
    static ColorModifier register(ColorModifier modifier) {
        ALL.put(modifier.name(), modifier);
        return modifier;
    }

    /**
     * A functional interface that defines a function that modifies a color based on a ColorContext
     */
    interface ModifierFunction {
        int apply(int color, ColorContext context);
    }

    record Instance(
        ColorModifier modifier,
        ColorContext context
    ) {
        public int apply(int color) {
            return modifier.function().apply(color, context);
        }
    }
}
