package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_PADDING_X;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Widget for a text element of a road sign
 */
public class TextWidget extends EditBox implements ElementProvider {
    protected static final int UNEDITABLE_COLOR = 0xFF5555;
    protected static final int TEXT_BOX_HEIGHT_SCALE = 4;
    /**
     * Padding between the text and the edge of the sign, in pixels.
     * Used to calculate max width of text fields.
     */
    protected static final int SIGN_PADDING = 1;

    protected TextElement text;
    protected final ColorResolver colorResolver;
    /**
     * Max text width in sign pixels
     */
    protected final int maxWidth;
    protected final int outlineColor;

    /**
     * Creates a new text element box.
     */
    public TextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth) {
        this(anchorX, anchorY, text, colorResolver, signWidth, GuiUtils.OUTLINE_COLOR);
    }

    /**
     * Creates a new text element box with a custom outline color.
     */
    public TextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth, int outlineColor) {
        // TODO: Maybe check other text fields to determine max width
        super(GuiUtils.font(), anchorX, anchorY,
                maxTextWidth(text, signWidth) * DEFAULT_TEXTURE_RENDER_SCALE,
                (int) (TEXT_BOX_HEIGHT_SCALE * DEFAULT_TEXTURE_RENDER_SCALE * text.scale()),
                Component.empty());
        // Calculate max width
        this.maxWidth = maxTextWidth(text, signWidth);
        this.text = text;
        this.colorResolver = colorResolver;
        this.outlineColor = outlineColor;
        // Calculate position
        this.setTextColor(colorResolver.resolveInt(text.color()));
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, text, this.width, this.height);
        this.setPosition(pos.x, pos.y);
        this.setValue(text.text());
        this.setResponder(this::onChange);
        // Unreadable in some cases, so skip for now:
        // this.setTextColor(text.backgroundColor());
    }

    // TODO: Maybe custom renderer to render like displayed?
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render text background
        if (text.backgroundColor() != null) {
            var backgroundColor = colorResolver.resolveInt(text.backgroundColor());
            guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, backgroundColor);
        }
        var textElementGraphics = new TextElementGuiGraphics(guiGraphics);
        super.renderWidget(textElementGraphics, mouseX, mouseY, partialTick);
//        if (text.backgroundColor() != null) {
//            var backgroundColor = colorResolver.resolve(text.backgroundColor()).getRGB();
//            guiGraphics.renderOutline(this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, backgroundColor);
//        }
        if (this.isHovered && this.active) {
            GuiUtils.renderOutlineOnTop(guiGraphics, this.getX(), this.getY(), this.width, this.height, outlineColor);
        }
        GuiUtils.renderOutlineOnTop(guiGraphics, this.getX() - 1, this.getY() - 1, this.width + 2, this.height + 2, Color.GRAY.getRGB());
    }

    protected void onChange(String value) {
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

    /**
     * Calculates the caret color from the text color
     *
     * @return the caret color
     */
    private int caretColor() {
        var textColor = colorResolver.resolveInt(text.color());
        var color = new Color(textColor, true);
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
        public int drawString(Font font, @Nullable String string, int x, int y, int color) {
            this.pose().pushPose();
            var scale = 1.33f;
            // Move pivot to (x, y)
            this.pose().translate(x, y, 0);
            // Scale around that point
            this.pose().scale(scale, scale, 1.0f);
            // Move back so text draws correctly
            this.pose().translate(-x, -y, 0);
            // Render caret with caret color
            if ("_".equals(string)) {
                color = caretColor();
            }
            // Render without shadow
            var result = super.drawString(font, string, x, y, color, false);
            this.pose().popPose();
            return result;
        }

        @Override
        public int drawString(Font font, FormattedCharSequence formattedCharSequence, int x, int y, int k) {
            this.pose().pushPose();
            var scale = 1.33f;
            // Move pivot to (x, y)
            this.pose().translate(x, y, 0);
            // Scale around that point
            this.pose().scale(scale, scale, 1.0f);
            // Move back so text draws correctly
            this.pose().translate(-x, -y, 0);
            var result = super.drawString(font, formattedCharSequence, x, y, k, false); // No shadow
            this.pose().popPose();
            return result;
        }
    }
}
