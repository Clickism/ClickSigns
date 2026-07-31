package de.clickism.clicksigns.gui.screen.edit.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class PanelWidget extends AbstractWidget {
    private static final int BACKGROUND_COLOR = new Color(0, 0, 0, 100).getRGB();
    private static final int OUTLINE_COLOR = Color.GRAY.getRGB();

    private final int backgroundColor;
    private final int outlineColor;

    public PanelWidget(int x, int y, int width, int height) {
        this(x, y, width, height, BACKGROUND_COLOR, OUTLINE_COLOR);
    }

    public PanelWidget(int x, int y, int width, int height, int backgroundColor, int outlineColor) {
        super(x, y, width, height, Component.empty());
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, backgroundColor);
        guiGraphics.renderOutline(getX(), getY(), width, height, outlineColor);
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
