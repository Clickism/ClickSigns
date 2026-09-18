package de.clickism.clicksigns.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

/**
 * Collection of utility methods for creating and manipulating Minecraft chat components.
 */
public class ComponentUtil {
    /**
     * Creates a translatable component with an icon prefix.
     *
     * @param icon the icon to prefix the text with
     * @param key  the translation key for the text
     * @return a component with the icon and translated text
     */
    public static Component t(String icon, String key, ChatFormatting... formatting) {
        return Component.literal(icon + " ")
            .append(Component.translatable(key))
            .withStyle(formatting);
    }

    /**
     * Creates a translatable component for the given translation key with specified formatting.
     *
     * @param key        the translation key for the text
     * @param formatting the formatting to apply to the text
     * @return a component with the translated text and specified formatting
     */
    public static Component t(String key, ChatFormatting... formatting) {
        return Component.translatable(key).withStyle(formatting);
    }

    /**
     * Creates a literal component for the given text with specified formatting.
     *
     * @param text       the text to display
     * @param formatting the formatting to apply to the text
     * @return a component with the literal text and specified formatting
     */
    public static Component l(String text, ChatFormatting... formatting) {
        return Component.literal(text).withStyle(formatting);
    }

    /**
     * Creates a translatable component for a confirmation button.
     *
     * @return a component with the translated text for "Confirm"
     */
    public static Component confirm() {
        return Component.translatable("clicksigns.confirm");
    }

    /**
     * Creates a translatable component for a confirmation button with an icon.
     *
     * @return a component with the icon and translated text for "Confirm"
     */
    public static Component confirmWithIcon() {
        return t("✔", "clicksigns.confirm");
    }
}
