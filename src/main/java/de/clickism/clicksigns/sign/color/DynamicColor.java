package de.clickism.clicksigns.sign.color;

/**
 * Used to mark {@link String} fields that represent a color in the dynamic color format,
 * which can be resolved using a {@link de.clickism.clicksigns.sign.color.ColorResolver}.
 *
 * <h1>Dynamic Color Format</h1>
 *
 * <h3>Named Colors</h3>
 * Named colors, e.g. <code>red</code>, <code>blue</code>, <code>green</code> are supported.
 * The default colors are defined in {@link DynamicColor#DEFAULT_COLOR_RESOLVER} and are always available.
 * <p>
 * Which named colors are available depends on which {@link ColorResolver} is used to resolve the color.
 * This is useful for defining custom colors for textures. For example, tile sets are intended
 * to define a <code>foreground</code> color, such that texts by default will use that color.
 *
 * <h3>Hex Colors</h3>
 * Hexadecimal color codes, e.g. <code>#rrggbb</code> or <code>#aarrggbb</code> are supported.
 *
 * <h3>Modifiers</h3>
 * Modifiers can be applied to colors using the <code>:</code> separator, after the color value.
 * For example, <code>red:a50</code> would apply a 50% alpha to the red color.
 * <p>
 * The following modifiers are supported:
 * <ul>
 *     <li><code>:a&lt;value&gt;</code> - Sets the alpha value (0-100)</li>
 *     <li><code>:l&lt;value&gt;</code> - Lightens the color (0-100)</li>
 *     <li><code>:d&lt;value&gt;</code> - Darkens the color (0-100)</li>
 * </ul>
 */
public @interface DynamicColor {
    /**
     * The default color resolver that provides a set of predefined colors.
     */
    ColorResolver DEFAULT_COLOR_RESOLVER = ColorResolver.empty()
        .define("text_light", 0xD0D0D0)
        .define("text_dark", 0x292929)
        .define("white", 0xFFFFFF)
        .define("black", 0x292929)
        .define("blue", 0x0D468A)
        .define("brown", 0x844635)
        .define("green", 0x009345)
        .define("yellow", 0xFFBC21)
        .define("red", 0xCE353A)
        .define("orange", 0xFF8C00);
}
