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

    private boolean leftBorder = true;
    private boolean rightBorder = true;
    private boolean topBorder = true;
    private boolean bottomBorder = true;

    public PanelWidget(int x, int y, int width, int height) {
        this(x, y, width, height, BACKGROUND_COLOR, OUTLINE_COLOR);
    }

    public PanelWidget(int x, int y, int width, int height, int backgroundColor, int outlineColor) {
        super(x, y, width, height, Component.empty());
        this.backgroundColor = backgroundColor;
        this.outlineColor = outlineColor;
    }

    public void borders(boolean left, boolean right, boolean top, boolean bottom) {
        this.leftBorder = left;
        this.rightBorder = right;
        this.topBorder = top;
        this.bottomBorder = bottom;
    }

    public void onlyBottomBorder() {
        this.borders(false, false, false, true);
    }

    public void onlyTopBorder() {
        this.borders(false, false, true, false);
    }

    public void onlyLeftBorder() {
        this.borders(true, false, false, false);
    }

    public void onlyRightBorder() {
        this.borders(false, true, false, false);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, backgroundColor);

        if (leftBorder) guiGraphics.fill(getX(), getY(), getX() + 1, getY() + height, outlineColor);
        if (rightBorder) guiGraphics.fill(getX() + width - 1, getY(), getX() + width, getY() + height, outlineColor);
        if (topBorder) guiGraphics.fill(getX(), getY(), getX() + width, getY() + 1, outlineColor);
        if (bottomBorder) guiGraphics.fill(getX(), getY() + height - 1, getX() + width, getY() + height, outlineColor);
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
