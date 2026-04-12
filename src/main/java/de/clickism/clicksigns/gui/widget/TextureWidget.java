package de.clickism.clicksigns.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

/**
 * Widget for rendering a road sign texture
 */
public class TextureWidget extends AbstractWidget {
    /**
     * The scale at which each pixel of the texture is rendered.
     */
    public static final int TEXTURE_RENDER_SCALE = 4;

    /**
     * The texture to render.
     */
    protected Texture texture;

    /**
     * Creates a new road sign texture widget.
     */
    public TextureWidget(int x, int y, Texture texture) {
        super(x, y, 0, 0, Component.empty());
        this.active = false;
        this.texture(texture);
    }

    /**
     * Sets the texture of the widget and updates its size accordingly.
     *
     * @param texture the new texture to set
     */
    public void texture(Texture texture) {
        this.texture = texture;
        updateSize();
    }

    private void updateSize() {
        this.width = texture.width() * TEXTURE_RENDER_SCALE;
        this.height = texture.height() * TEXTURE_RENDER_SCALE;
    }

    /**
     * Centers the widget on its current position.
     */
    public void center() {
        this.setX(this.getX() - this.width / 2);
        this.setY(this.getY() - this.height / 2);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Override render to enable blending for semi-transparent textures (i.E: for arrows)
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.blit(
                this.texture.location(),
                this.getX(),
                this.getY(),
                0, 0,
                this.width, this.height,
                this.width, this.height
        );
        RenderSystem.disableBlend();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // No narration
    }
}
