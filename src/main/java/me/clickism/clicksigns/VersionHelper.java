package me.clickism.clicksigns;

import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.util.Identifier;

public class VersionHelper {
    
    public static IconWidget createIconWidget(int x, int y, Identifier texture, int iconWidth, int iconHeight) {
        //? if >=1.20.5 {
        return IconWidget.create(x, y, texture, iconWidth, iconHeight);
        //?} else
        /*return new IconWidget(x, y, iconWidth, iconHeight, texture);*/
    }
}
