package de.clickism.clicksigns.util;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class ComponentUtil {
    /**
     * Creates a translatable tooltip component.
     *
     * @param key the translation key for the tooltip text
     * @return a tooltip component with the translated text
     */
    public static Tooltip translatableTooltip(String key) {
        return Tooltip.create(Component.translatable(key));
    }

    /**
     * Creates a translatable component with an icon prefix.
     *
     * @param icon the icon to prefix the text with
     * @param key  the translation key for the text
     * @return a component with the icon and translated text
     */
    public static Component translatableWithIcon(String icon, String key) {
        return Component.literal(icon + " ")
                .append(Component.translatable(key));
    }

    /**
     * Creates a translatable component for a confirmation button.
     *
     * @return a component with the translated text for "Confirm"
     */
    public static Component confirm() {
        return Component.translatable("clicksigns.text.confirm");
    }

    /**
     * Creates a translatable component for a confirmation button with an icon.
     *
     * @return a component with the icon and translated text for "Confirm"
     */
    public static Component confirmWithIcon() {
        return translatableWithIcon("✔", "clicksigns.text.confirm");
    }

    /**
     * Renders a component to a string representation.
     *
     * @param component the component to render
     * @return the string representation of the component
     */
    public static String render(Component component) {
        return component.getString();
    }
}
