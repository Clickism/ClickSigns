package de.clickism.clicksigns.gui.widget.texture;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A clickable texture widget.
 * Renders an outline on hover, and can be used as a button.
 */
public class ClickableTextureWidget extends TextureWidget {
    protected int outlineColor;
    protected boolean renderOutlineOnHover = true;

    /**
     * Creates a new clickable texture widget.
     *
     * @param x            the x position of the widget
     * @param y            the y position of the widget
     * @param texture      the texture to render
     * @param outlineColor the color of the outline to render on hover
     */
    public ClickableTextureWidget(int x, int y, Texture texture, int outlineColor) {
        this(x, y, texture, outlineColor, DEFAULT_TEXTURE_RENDER_SCALE);
    }

    /**
     * Creates a new clickable texture widget.
     *
     * @param x            the x position of the widget
     * @param y            the y position of the widget
     * @param texture      the texture to render
     * @param outlineColor the color of the outline to render on hover
     * @param renderScale  the scale at which each pixel of the texture is rendered
     */
    public ClickableTextureWidget(int x, int y, Texture texture, int outlineColor, int renderScale) {
        super(x, y, texture, renderScale);
        this.outlineColor = outlineColor;
        this.active = true;
        this.clickable = true;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (BaseScreen.isHovered(this, mouseX, mouseY) && this.active && this.renderOutlineOnHover) {
            GuiUtils.renderOutlineOnTop(guiGraphics, this.getX(), this.getY(), this.width, this.height, outlineColor);
        }
    }
}
