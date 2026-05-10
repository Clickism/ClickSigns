package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.AbstractTextBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;

public class SignTextBox extends AbstractTextBox {
    public SignTextBox(int x, int y, int width, int height, Font font) {
        super(x, y, width, height, font);
    }

    @Override
    protected void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        int x = getX();
        int y = getY();
        // Temporary background
        guiGraphics.fill(x, y, x + width, y + height, 0xFF000000);
        // Visible text from display pos to end, truncated to fit in view
        String visible = font.plainSubstrByWidth(value.substring(displayPos), width);
        int relativeCursor = cursorPos - displayPos;
        int relativeHighlight = highlightPos - displayPos;
        // Draw text
        int textX = x;
        int textY = y + height - font.lineHeight;

        String left = visible.substring(0, relativeCursor);
        String right = visible.substring(relativeCursor);
        // Render left of cursor
        guiGraphics.drawString(font, left, textX, textY, textColor, false);
        textX += font.width(left);
        // Render cursor
        long time = System.currentTimeMillis();
        var cursorBlinking = time / 300 % 2 == 0; // Blink every 300 ms
        if (this.isFocused() && !cursorBlinking) {
            boolean lineCursor = cursorPos < value.length();
            if (lineCursor) {
                var lineColor = 0xFFFFFFFF; // TODO: Configurable cursor color
                guiGraphics.fill(RenderType.guiOverlay(), textX, textY, textX + 1, textY + font.lineHeight, lineColor);
            } else {
                // Underscore cursor
                var cursor = "_";
                guiGraphics.drawString(font, cursor, textX, textY, textColor, false);
                textX += font.width(cursor);
            }
        }
        // Render right of cursor
        guiGraphics.drawString(font, right, textX, textY, textColor, false);
        // Outline if focused
        if (this.isFocused()) {
            GuiUtils.renderOutlineOnTop(guiGraphics, x - 1, y - 1, width + 2, height + 2, GuiUtils.OUTLINE_COLOR);
        }
    }
}
