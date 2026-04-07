package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.gui.components.ImageWidget;

/**
 * Widget for rendering a road sign texture
 */
public class TextureWidget extends ImageWidget {
    /**
     * The scale at which each pixel of the texture is rendered.
     */
    public static final float TEXTURE_RENDER_SCALE = 4f;

    /**
     * Creates a new road sign texture widget.
     */
    public TextureWidget(int x, int y, Texture texture) {
        super(x, y, (int) (texture.width() * TEXTURE_RENDER_SCALE), (int) (texture.height() * TEXTURE_RENDER_SCALE), texture.location());
        this.active = false;
    }

    /**
     * Centers the widget on its current position.
     */
    public void center() {
        this.setX(this.getX() - this.width / 2);
        this.setY(this.getY() - this.height / 2);
    }
}
