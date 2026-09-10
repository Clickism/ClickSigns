package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clickui.UiColor;

public class UiUtil {
    private UiUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Returns the primary color of the given texture as a UiColor.
     *
     * @param texture the texture to get the primary color from
     * @return the primary color of the texture as a UiColor, or default color if no primary color
     */
    public static UiColor primaryColorOf(Texture texture) {
        var primaryColor = texture.primaryColor();
        if (primaryColor != null) {
            return UiColor.rgba(primaryColor);
        }
        return UiColor.BLACK_A50;
    }
}
