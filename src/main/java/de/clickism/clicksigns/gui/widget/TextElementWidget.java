package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;

/**
 * Widget for a text element of a road sign
 */
public class TextElementWidget extends EditBox implements ElementProvider {
    private static final int TEXT_BOX_HEIGHT_SCALE = 4;

    private TextElement text;

    /**
     * Creates a new text element box.
     */
    public TextElementWidget(int anchorX, int anchorY, TextElement text) {
        // TODO: Calculate width properly
        super(GuiUtils.font(), anchorX, anchorY, 100, (int) (TEXT_BOX_HEIGHT_SCALE * TEXTURE_RENDER_SCALE * text.scale()), Component.empty());
        this.text = text;
        // Calculate position
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, text, this.width, this.height);
        this.setPosition(pos.x, pos.y);
        this.setValue(text.text());
        this.setResponder(this::onChange);
    }

    private void onChange(String value) {
        this.text = this.text.withText(value);
    }

    @Override
    public RoadSignElement element() {
        return text;
    }
}
