package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class EditTextWidget extends TextWidget {
    public EditTextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth) {
        super(anchorX, anchorY, text, colorResolver, signWidth);
        setTooltip(Tooltip.create(Component.literal("§f§lClick+drag §rto move element")));
    }
}
