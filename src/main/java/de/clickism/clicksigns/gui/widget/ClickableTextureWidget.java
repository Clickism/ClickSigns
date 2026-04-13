package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A clickable texture widget.
 * Renders an outline on hover, and can be used as a button.
 */
public class ClickableTextureWidget extends TextureWidget {
    private final int outlineColor;

    /**
     * Creates a new clickable texture widget.
     *
     * @param x            the x position of the widget
     * @param y            the y position of the widget
     * @param texture      the texture to render
     * @param outlineColor the color of the outline to render on hover
     */
    public ClickableTextureWidget(int x, int y, Texture texture, int outlineColor) {
        super(x, y, texture);
        this.outlineColor = outlineColor;
        this.active = true;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isHovered) {
            renderOutline(guiGraphics);
        }
    }

    /**
     * Renders an outline around the widget.
     *
     * @param guiGraphics the graphics context to render with
     */
    public void renderOutline(GuiGraphics guiGraphics) {
        renderOutline(guiGraphics, outlineColor);
    }

    /**
     * Renders an outline around the widget with the specified color.
     *
     * @param guiGraphics the graphics context to render with
     * @param color       the color of the outline
     */
    public void renderOutline(GuiGraphics guiGraphics, int color) {
        GuiUtils.renderHoverOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height, color);
    }
}
