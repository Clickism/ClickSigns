package de.clickism.clicksigns.gui.screen.edit;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class PanelWidget extends AbstractWidget {
    private static final int BACKGROUND_COLOR = new Color(0, 0, 0, 100).getRGB();
    private static final int OUTLINE_COLOR = Color.GRAY.getRGB();

    public PanelWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, BACKGROUND_COLOR);
        guiGraphics.renderOutline(getX(), getY(), width, height, OUTLINE_COLOR);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // No narration
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        // Nothing
        return false;
    }
}
