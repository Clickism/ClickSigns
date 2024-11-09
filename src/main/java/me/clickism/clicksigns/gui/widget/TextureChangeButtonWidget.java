package me.clickism.clicksigns.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TextureChangeButtonWidget extends ButtonWidget {
    public TextureChangeButtonWidget(int size, Text text, PressAction onPress) {
        super(0, 0, size, size, text, onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
    }
}
