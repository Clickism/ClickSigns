package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.List;

import static de.clickism.clicksigns.gui.GuiUtils.OUTLINE_COLOR;
import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;

/**
 * Widget for a text element of a road sign
 */
public class TextElementWidget extends EditBox implements ElementProvider {
    private static final int TEXT_BOX_HEIGHT_SCALE = 4;
    public static final Tooltip TOOLTIP = Tooltip.create(Component.literal("§lClick §rto edit text\n§lShift+Click §rto change color"));
    public static final int SCALE_BUFFER = 4;

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
        // Unreadable in some cases, so skip for now:
        // this.setTextColor(text.backgroundColor());
        this.setTooltip(TOOLTIP);
    }

    // TODO: Maybe custom renderer to render like displayed?
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.renderOutline(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, text.backgroundColor());
        if (this.isMouseOnScaleZone(mouseX, mouseY)) {
            // Render scale tooltip
            this.setTooltip(Tooltip.create(Component.literal("§lClick and drag §rto scale text")));
            guiGraphics.fill(this.getX() + this.width - SCALE_BUFFER, this.getY(), this.getX() + this.width, this.getY() + this.height, Color.GREEN.getRGB());
        } else {
            this.setTooltip(TOOLTIP);
            if (this.isHovered) {
                GuiUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height);
            }
        }
    }

    private boolean isMouseOnScaleZone(int mouseX, int mouseY) {
        if (mouseY < this.getY() || mouseY > this.getY() + this.height) return false;
        // Check if the mouse is on the left or right corner of the text box
        int boxEnd = this.getX() + this.width;
        int boxStart = this.getX();
        int rightZoneStart = this.getX() + this.width - SCALE_BUFFER;
        int leftZoneEnd = this.getX() + SCALE_BUFFER;
        return (mouseX >= rightZoneStart && mouseX <= boxEnd) || (mouseX >= boxStart && mouseX <= leftZoneEnd);
    }

    private void onChange(String value) {
        this.text = this.text.withText(value);
    }

    @Override
    public RoadSignElement element() {
        return text;
    }
}
