package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.AbstractTextBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.*;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * A text box that renders the text for editing sign text elements.
 */
public class SignTextBox extends AbstractTextBox {
    private final float textScale;

    public SignTextBox(int x, int y, int width, int height, Font font, float textScale) {
        super(x, y, width, height, font);
        this.textScale = textScale;
    }

    // TODO: Clean up and refactor
    @Override
    protected void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int x = getX();
        int y = getY();
        // Temporary background
        guiGraphics.fill(x, y + 1, x + width, y + height + 1, backgroundColor);

        // Visible text from display pos to end, truncated to fit in view
        // TODO: Make sure placeholder is cut off properly
        boolean showingPlaceholder = this.value.isEmpty() && !this.isFocused();
        String value = showingPlaceholder ? placeholder : this.value;
        String visible = font.plainSubstrByWidth(value.substring(displayPos), width);
        int relativeCursor = cursorPos - displayPos;
        int relativeHighlight = highlightPos - displayPos;

        // Draw text
        guiGraphics.pose().pushPose();
        var scale = BLOCK_PIXELS * TEXT_RENDER_SCALE * textScale * DEFAULT_TEXTURE_RENDER_SCALE;
        // Move pivot to (x, y)
        guiGraphics.pose().translate(x, y, 0);
        // Scale around that point
        guiGraphics.pose().scale(scale, scale, 1.0f);
        // Move back so text draws correctly
        guiGraphics.pose().translate(-x, -y, 0);

        int textX = x;
        // Remove padding for accents, to center visually
        // Actual padding should be 2, but 1 makes it so text is slightly higher as opposed to
        // slightly lower, so looks more aligned this way
        float mainLineHeight = font.lineHeight - 1;
        int textY = (int) (y + height / 2f - mainLineHeight * scale / 2f);

        String left = visible.substring(0, relativeCursor);
        String right = visible.substring(relativeCursor);
        String cursor = "_";

        // Render background
        int paddingX = 2;
        int paddingY = 1;
        if (backgroundColor != 0) {
            textX += paddingX;
        }
        // GuiUtils.renderOutlineOnTop(guiGraphics, textX, textY, font.width(visible), font.lineHeight - 2, GuiUtils.OUTLINE_COLOR);

        // Render left of cursor
        var highlightColor = textColor & 0xFFFFFF | 0x55000000; // Set alpha
        var textColor = showingPlaceholder ? highlightColor : this.textColor;
        guiGraphics.drawString(font, left, textX, textY, textColor, false);

        textX += font.width(left);
        // Render cursor
        long time = System.currentTimeMillis();
        var cursorBlinking = time / 300 % 2 == 0; // Blink every 300 ms
        if (this.isFocused() && !cursorBlinking) {
            boolean lineCursor = cursorPos < value.length();
            if (lineCursor) {
                guiGraphics.fill(RenderType.guiOverlay(), textX, textY, textX + 1, textY + font.lineHeight, textColor);
            } else {
                // Underscore cursor
                guiGraphics.drawString(font, cursor, textX, textY, textColor, false);
                textX += font.width(cursor);
            }
        }
        // Render right of cursor
        guiGraphics.drawString(font, right, textX, textY, textColor, false);
        // Render selection
        if (relativeHighlight != relativeCursor) {
            int highlightStart = Math.min(relativeCursor, relativeHighlight);
            int highlightEnd = Math.max(relativeCursor, relativeHighlight);
            int highlightX = x + font.width(visible.substring(0, highlightStart));
            int highlightWidth = font.width(visible.substring(highlightStart, highlightEnd));
            guiGraphics.fill(RenderType.guiOverlay(), highlightX, textY, highlightX + highlightWidth, textY + font.lineHeight, highlightColor); // TODO: Configurable selection color
        }
        guiGraphics.pose().popPose();

        // Render underline
        var lineX = backgroundColor != 0 ? x + paddingX : x;
        var lineWidth = backgroundColor != 0 ? width - paddingX * 2 : width;
        GuiUtils.renderOutline(guiGraphics, lineX, y + height - 1, lineWidth, 1, textColor);
        // TODO: Fix underline disappears if text too long

//        // Outline if focused
//        if (this.isFocused()) {
//            GuiUtils.renderOutlineOnTop(guiGraphics, x - 1, y, width + 2, height + 1, GuiUtils.OUTLINE_COLOR);
//        }
    }
}
