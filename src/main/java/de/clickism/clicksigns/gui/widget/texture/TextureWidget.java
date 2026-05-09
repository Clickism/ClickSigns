package de.clickism.clicksigns.gui.widget.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Widget for rendering a road sign texture
 */
public class TextureWidget extends AbstractWidget {
    /**
     * The scale at which each pixel of the texture is rendered.
     */
    public static final int DEFAULT_TEXTURE_RENDER_SCALE = 4;

    /**
     * The texture to render.
     */
    protected @Nullable Texture texture;
    protected boolean clickable = false;
    protected int renderScale;

    /**
     * Creates a new texture widget.
     *
     * @param x       the x position of the widget
     * @param y       the y position of the widget
     * @param texture the texture to render, can be null for an empty widget
     */
    public TextureWidget(int x, int y, @Nullable Texture texture) {
        this(x, y, texture, DEFAULT_TEXTURE_RENDER_SCALE);
    }

    /**
     * Creates a new texture widget with a custom render scale.
     *
     * @param x           the x position of the widget
     * @param y           the y position of the widget
     * @param texture     the texture to render, can be null for an empty widget
     * @param renderScale the scale at which each pixel of the texture is rendered
     */
    public TextureWidget(int x, int y, @Nullable Texture texture, int renderScale) {
        super(x, y, 0, 0, Component.empty());
        this.active = false;
        this.renderScale = renderScale;
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
        if (this.texture == null) {
            this.width = 0;
            this.height = 0;
            return;
        }
        this.width = texture.width() * renderScale;
        this.height = texture.height() * renderScale;
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
        if (this.texture == null) return;
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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouse) {
        if (!this.clickable) {
            // Don't consume click
            return false;
        }
        return super.mouseClicked(mouseX, mouseY, mouse);
    }
}
