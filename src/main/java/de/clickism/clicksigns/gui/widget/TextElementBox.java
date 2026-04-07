package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;

/**
 * GUI element for a text element of a sign
 */
public class TextElementBox extends EditBox {
    private static final float TEXT_BOX_HEIGHT = 4f;

    private final TextElement textElement;

    /**
     * Creates a new text element box.
     */
    public TextElementBox(TextElement textElement, int rootX, int rootY) {
        super(GuiUtils.font(), rootX, rootY, 100, (int) (TEXT_BOX_HEIGHT * TEXTURE_RENDER_SCALE * textElement.scale()), Component.empty());
        this.textElement = textElement;
        // Calculate position
        int xOffset = (int) (textElement.localX() * TEXTURE_RENDER_SCALE);
        int yOffset = (int) (textElement.localY() * TEXTURE_RENDER_SCALE);
        this.setX(rootX + xOffset);
        this.setY(rootY + yOffset - this.height);
    }
}
