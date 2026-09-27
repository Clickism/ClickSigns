package de.clickism.clicksigns.sign.reload.sign;

import de.clickism.clicksigns.sign.color.ColorResolver;

import java.util.HashMap;

/**
 * Represents a color definition that maps color names to their corresponding color values.
 */
public class ColorDefinition extends HashMap<String, String> {
    /**
     * Converts this color definition to a ColorResolver.
     *
     * @return a ColorResolver that can resolve colors based on this definition
     */
    public ColorResolver toColorResolver() {
        var resolver = ColorResolver.withDefault();
        this.forEach(resolver::tryParseAndDefine);
        return resolver;
    }
}
