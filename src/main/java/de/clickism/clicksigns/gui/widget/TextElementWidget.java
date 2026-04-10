package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static de.clickism.clicksigns.gui.GuiUtils.OUTLINE_COLOR;
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
        this.setTextColor(text.backgroundColor());

        this.setTooltip(Tooltip.create(Component.literal("§lClick §rto edit text\n§lShift+Click §rto change color")));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isHovered) {
            guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, OUTLINE_COLOR);
        }
    }

    private void onChange(String value) {
        this.text = this.text.withText(value);
    }

    @Override
    public RoadSignElement element() {
        return text;
    }
}
