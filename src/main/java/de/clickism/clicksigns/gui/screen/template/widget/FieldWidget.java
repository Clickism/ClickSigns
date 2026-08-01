package de.clickism.clicksigns.gui.screen.template.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.widget.PanelWidget;
import de.clickism.clicksigns.gui.util.NestedWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class FieldWidget extends NestedWidget {
    public FieldWidget(int x, int y, Component header, Component text, int maxWidth) {
        super(x, y);

        int padding = 4;
        int lineOffset = GuiUtils.font().lineHeight / 2;
        var headerWidget = new StringWidget(x + padding, y + lineOffset + padding, 0, 0, header, GuiUtils.font());
        headerWidget.setColor(new Color(255, 255, 255, 100).getRGB());
        headerWidget.alignLeft();
        headerWidget.setAlpha(0.6f);
        addChild(headerWidget);


        var textWidget = new MultiLineTextWidget(x + padding, y + lineOffset + padding + 10, text, GuiUtils.font());
        textWidget.setMaxWidth(maxWidth - padding * 2);
        addChild(textWidget);

        var width = Math.max(GuiUtils.font().width(header), GuiUtils.font().width(text)) + padding * 2;
        var height = (textWidget.getY() + textWidget.getHeight()) - y + padding;
        var panel = new PanelWidget(x, y, width, height, new Color(0, 0, 0, 100).getRGB(), 0);
        addChild(panel);

        updateSizeAndPosition();
    }
}
