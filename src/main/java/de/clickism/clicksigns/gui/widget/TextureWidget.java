package de.clickism.clicksigns.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;

/**
 * Widget for rendering a road sign texture
 */
public class TextureWidget extends ImageWidget {
    /**
     * The scale at which each pixel of the texture is rendered.
     */
    public static final int TEXTURE_RENDER_SCALE = 4;

    private final Texture texture;

    /**
     * Creates a new road sign texture widget.
     */
    public TextureWidget(int x, int y, Texture texture) {
        super(x, y, texture.width() * TEXTURE_RENDER_SCALE, texture.height() * TEXTURE_RENDER_SCALE, texture.location());
        this.active = false;
        this.texture = texture;
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
}
