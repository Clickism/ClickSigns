package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.ColorResolver;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;

/**
 * Widget for a text element of a road sign
 */
public class TextElementWidget extends EditBox implements ElementProvider {
    private static final int TEXT_BOX_HEIGHT_SCALE = 4;

    private TextElement text;
    private final ColorResolver colorResolver;

    /**
     * Creates a new text element box.
     */
    public TextElementWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver) {
        // TODO: Calculate width properly
        super(GuiUtils.font(), anchorX, anchorY, 100, (int) (TEXT_BOX_HEIGHT_SCALE * TEXTURE_RENDER_SCALE * text.scale()), Component.empty());
        this.text = text;
        this.colorResolver = colorResolver;
        // Calculate position
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, text, this.width, this.height);
        this.setPosition(pos.x, pos.y);
        this.setValue(text.text());
        this.setResponder(this::onChange);
        // Unreadable in some cases, so skip for now:
        // this.setTextColor(text.backgroundColor());
        this.setTooltip(Tooltip.create(Component.literal("§lClick §rto edit text\n§lShift+Click §rto change color")));
    }

    // TODO: Maybe custom renderer to render like displayed?
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (text.backgroundColor() != null) {
            var backgroundColor = colorResolver.resolve(text.backgroundColor()).getRGB();
            guiGraphics.renderOutline(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, backgroundColor);
        }
        if (this.isHovered) {
            GuiUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height);
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
