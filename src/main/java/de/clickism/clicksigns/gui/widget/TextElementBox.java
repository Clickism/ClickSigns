package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;

/**
 * GUI element for a text element of a sign
 */
public class TextElementBox extends EditBox {
    private static final float TEXT_BOX_HEIGHT = 4f;

    private final TextElement text;

    /**
     * Creates a new text element box.
     */
    public TextElementBox(TextElement text, int rootX, int rootY) {
        super(GuiUtils.font(), rootX, rootY, 100, (int) (TEXT_BOX_HEIGHT * TEXTURE_RENDER_SCALE * text.scale()), Component.empty());
        this.text = text;
        // Calculate position
        int offsetX = (int) (text.localX() * TEXTURE_RENDER_SCALE);
        int offsetY = (int) (text.localY() * TEXTURE_RENDER_SCALE);
        // By default, should be centered
        offsetX -= this.width / 2;
        offsetY -= this.height / 2;
        // Apply alignment offset
        offsetX += (int) (text.alignment().offset().x * width);
        offsetY += (int) (text.alignment().offset().y * height);
        // Set position
        this.setX(rootX + offsetX);
        this.setY(rootY + offsetY - this.height);
    }
}
