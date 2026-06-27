package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.AbstractTextBox;
import de.clickism.clicksigns.sign.Alignment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * A text box that renders the text for editing sign text elements.
 */
public class SignTextBox extends AbstractTextBox {
    private final float textScale;
    private final Alignment alignment;

    public SignTextBox(int x, int y, int width, int height, Font font, float textScale, Alignment alignment) {
        super(x, y, width, height, font);
        this.textScale = textScale;
        this.alignment = alignment;
    }

    private int calculateTextX(int x, float scale) {
        int textWidth = font.width(value);
        int offset = (int) -alignment.offset().x() + 1; // Add 1 since left should be 0
        return (int) Math.ceil(x + offset * (width) / (2 * scale) - offset * textWidth / 2f);
    }

    // TODO: Clean up and refactor
    // TODO: Make sure placeholder is cut off properly
    @Override
    protected void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int x = getX();
        int y = getY();

        // Background color
        guiGraphics.fill(x, y + 1, x + width, y + height + 1, backgroundColor);

        // Visible text from display pos to end, truncated to fit in view
        boolean showPlaceholder = this.value.isEmpty() && !this.isFocused();
        String text = showPlaceholder ? placeholder : this.value;

        // Draw text
        guiGraphics.pose().pushPose();
        var scale = BLOCK_PIXELS * TEXT_RENDER_SCALE * textScale * DEFAULT_TEXTURE_RENDER_SCALE;
        // Move pivot to (x, y)
        guiGraphics.pose().translate(x, y, 0);
        // Scale around that point
        guiGraphics.pose().scale(scale, scale, 1.0f);
        // Move back so text draws correctly
        guiGraphics.pose().translate(-x, -y, 0);

        int textX = calculateTextX(x, scale);
        int textXStart = textX;
        // Remove padding for accents, to center visually
        // Actual padding should be 2, but 1 makes it so text is slightly higher as opposed to
        // slightly lower, so looks more aligned this way
        float mainLineHeight = font.lineHeight - 1;
        int textY = (int) (y + height / 2f - mainLineHeight * scale / 2f);

        String left = text.substring(0, cursorPos);
        String right = text.substring(cursorPos);
        String cursor = "_";

        // Render background
        int paddingX = 2;
        if (backgroundColor != 0) {
            textX += paddingX;
            textXStart += paddingX;
        }

        // Render left of cursor
        var highlightColor = textColor & 0xFFFFFF | 0x55000000; // Set alpha
        var textColor = showPlaceholder ? highlightColor : this.textColor;
        guiGraphics.drawString(font, left, textX, textY, textColor, false);

        textX += font.width(left);

        // Render cursor
        long time = System.currentTimeMillis();
        var cursorBlinking = time / 300 % 2 == 0; // Blink every 300 ms
        if (this.isFocused() && this.editable && !cursorBlinking) {
            boolean lineCursor = cursorPos < text.length();
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
        if (highlightPos != cursorPos) {
            int highlightStart = Math.min(cursorPos, highlightPos);
            int highlightEnd = Math.max(cursorPos, highlightPos);
            int highlightX = textXStart + font.width(text.substring(0, highlightStart));
            int highlightWidth = font.width(text.substring(highlightStart, highlightEnd));
            guiGraphics.fill(RenderType.guiOverlay(), highlightX, textY, highlightX + highlightWidth, textY + font.lineHeight, highlightColor); // TODO: Configurable selection color
        }
        guiGraphics.pose().popPose();

        // Render underline
        var lineX = backgroundColor != 0 ? x + paddingX : x;
        var lineWidth = backgroundColor != 0 ? width - paddingX * 2 : width;
        GuiUtils.renderOutline(guiGraphics, lineX, y + height - 1, lineWidth, 1, textColor);
    }
}
