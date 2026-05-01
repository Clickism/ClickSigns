package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.gui.widget.TextureWidget.TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_PADDING_X;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Widget for a text element of a road sign
 */
public class TextElementWidget extends EditBox implements ElementProvider {
    private static final int UNEDITABLE_COLOR = 0xFF5555;
    private static final int TEXT_BOX_HEIGHT_SCALE = 4;
    /**
     * Padding between the text and the edge of the sign, in pixels.
     * Used to calculate max width of text fields.
     */
    private static final int SIGN_PADDING = 1;

    private TextElement text;
    private final ColorResolver colorResolver;
    /**
     * Max text width in sign pixels
     */
    private final int maxWidth;

    /**
     * Creates a new text element box.
     */
    public TextElementWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth) {
        // TODO: Maybe check other text fields to determine max width
        super(GuiUtils.font(), anchorX, anchorY,
                maxTextWidth(text, signWidth) * TEXTURE_RENDER_SCALE,
                (int) (TEXT_BOX_HEIGHT_SCALE * TEXTURE_RENDER_SCALE * text.scale()),
                Component.empty());
        // Calculate max width
        this.maxWidth = maxTextWidth(text, signWidth);
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
        if (this.isHovered && this.active) {
            GuiUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width, this.height);
        }
    }

    private void onChange(String value) {
        if (renderWidthOf(value) > this.maxWidth) {
            // Text too big, trim it and set text color to red
            value = value.substring(0, value.length() - 1);
            this.setValue(value);
            this.setTextColor(UNEDITABLE_COLOR);
        } else {
            this.setTextColor(DEFAULT_TEXT_COLOR);
        }
        // Update text element with new text
        this.text = this.text.withText(value);
    }

    @Override
    public SignElement element() {
        return text;
    }

    /**
     * Makes the widget uneditable, disabling interaction and removing the tooltip.
     */
    public void makeUneditable() {
        this.setTooltip(null);
        this.active = false;
    }

    /**
     * Get the width of the text when rendered on a sign.
     *
     * @param string the text to measure.
     * @return the rendered width of the text.
     */
    private float renderWidthOf(String string) {
        return GuiUtils.font().width(string) * BLOCK_PIXELS * TEXT_RENDER_SCALE * this.text.scale();
    }

    /**
     * Calculates the max text width in sign pixels
     */
    private static int maxTextWidth(TextElement text, int signWidth) {
        if (text.backgroundColor() != null) {
            // If there is a background color, we need to account for the outline, which is 1 pixel wide
            return signWidth - text.localX() - SIGN_PADDING - (int) (TEXT_PADDING_X * 2);
        }
        return signWidth - text.localX() - SIGN_PADDING;
    }
}
