package de.clickism.clicksigns.sign.color;

import net.minecraft.util.Mth;

import java.util.List;

public record ColorContext(
    ColorResolver colorResolver,
    List<String> arguments
) {
    public String argumentAt(int index) {
        if (index >= arguments.size()) {
            return "";
        }
        return arguments.get(index);
    }

    public String argumentAtOrDefault(int index, String defaultValue) {
        if (index >= arguments.size()) {
            return defaultValue;
        }
        return arguments.get(index);
    }

    public float percentageAt(int index) {
        if (index >= arguments.size()) {
            return 0f;
        }
        var arg = argumentAt(index);
        try {
            var value = Integer.parseInt(arg);
            value = Mth.clamp(value, 0, 100);
            return value / 100f;
        } catch (NumberFormatException e) {
            return 0f;
        }
    }
}
