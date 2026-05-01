package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;


public class CategoryHeaderWidget extends StringWidget {
    public CategoryHeaderWidget(int width, String text) {
        super(0, 0, width, 24, Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE), GuiUtils.font());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(guiGraphics, mouseX, mouseY, delta);
        var padding = 32;
//        GuiUtils.renderHorizontalLine(guiGraphics, this.getX() + padding, this.getX() + this.width - padding, this.getY() + this.height - 6, 1, Color.GRAY.getRGB());

    }
}
