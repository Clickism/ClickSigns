package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

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
        this.setTextColor(text.color());
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
        // Render text background
        if (text.backgroundColor() != 0) {
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, text.backgroundColor());
        }
        var textElementGraphics = new TextElementGuiGraphics(guiGraphics);
        super.renderWidget(textElementGraphics, mouseX, mouseY, partialTick);
        // Hover outline
        if (this.isHovered) {
            GuiUtils.renderHoverOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height);
        }
    }

    /**
     * Updates the text element with the new value from the edit box
     *
     * @param value the new text value from the edit box
     */
    private void onChange(String value) {
        this.text = this.text.withText(value);
    }

    @Override
    public RoadSignElement element() {
        return text;
    }

    /**
     * Calculates the caret color from the text color
     *
     * @return the caret color
     */
    private int caretColor() {
        var color = new Color(text.color(), true);
        color = color.brighter().brighter();
        return color.getRGB();
    }

    /**
     * Custom hacky gui graphics context to "mixin" into the EditBox rendering
     * logic. Disabled background, text shadows, and uses caret color properly.
     */
    // TODO: Maybe just override the render logic instead?
    // TODO: Caret color is bad when highlighted text!
    private class TextElementGuiGraphics extends GuiGraphics {
        /**
         * Creates a new text element gui graphics context
         *
         * @param guiGraphics the gui graphics context to get the buffer source from
         */
        public TextElementGuiGraphics(GuiGraphics guiGraphics) {
            super(Minecraft.getInstance(), guiGraphics.bufferSource());
        }

        @Override
        public void fill(RenderType renderType, int i, int j, int k, int l, int m, int color) {
            // Render text highlight with the same color
            if (renderType.equals(RenderType.guiTextHighlight()) ||
                renderType.equals(RenderType.guiOverlay())) {
                super.fill(renderType, i, j, k, l, m, caretColor());
            }
            // No fill otherwise
        }

        @Override
        public int drawString(Font font, @Nullable String string, int i, int j, int color) {
            // Render caret with caret color
            if ("_".equals(string)) {
                color = caretColor();
            }
            // Render without shadow
            return super.drawString(font, string, i, j, color, false);
        }

        @Override
        public int drawString(Font font, FormattedCharSequence formattedCharSequence, int i, int j, int k) {
            return super.drawString(font, formattedCharSequence, i, j, k, false); // No shadow
        }
    }
}
