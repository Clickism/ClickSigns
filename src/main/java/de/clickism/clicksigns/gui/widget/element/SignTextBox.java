package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.AbstractTextBox;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * A text box that renders the text for editing sign text elements.
 */
public class SignTextBox extends AbstractTextBox {
    private static final String CURSOR = "_";
    private static final int MIN_BOX_WIDTH = 4;
    private static final int BACKGROUND_PADDING = 3;

    private final float renderScale;

    public SignTextBox(int x, int y, int width, int height, Font font, float textScale) {
        super(x, y, width, height, font);
        this.renderScale = BLOCK_PIXELS * TEXT_RENDER_SCALE * textScale * DEFAULT_TEXTURE_RENDER_SCALE;
        this.width = currentWidth();
        addListener(value -> {
            updateWidth();
        });
    }

    protected void updateWidth() {
        this.width = currentWidth();
    }

    @Override
    public void setFocused(boolean bl) {
        super.setFocused(bl);
        this.width = currentWidth();
    }

    protected int currentWidth() {
        String text = textToShow();
        float width = font.width(text);
        if (listening()) {
            width += font.width(CURSOR);
        }
        width *= renderScale;
        if (backgroundColor != 0) {
            width += BACKGROUND_PADDING * 2;
        }
        if (width < MIN_BOX_WIDTH) {
            width = MIN_BOX_WIDTH;
        }
        return Mth.ceil(width);
    }

    protected boolean showingPlaceholder() {
        return this.value.isEmpty() && !listening();
    }

    protected String textToShow() {
        return showingPlaceholder() ? placeholder : this.value;
    }

    protected boolean hasBackground() {
        return backgroundColor != 0;
    }

    protected int highlightColor() {
        return textColor & 0xFFFFFF | 0x55000000; // Set alpha for highlight
    }

    protected int colorToShow() {
        return showingPlaceholder() ? highlightColor() : this.textColor;
    }

    @Override
    protected void renderText(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // Layout
        final int textX = hasBackground()
                ? getX() + BACKGROUND_PADDING - 1 // -1 makes it better aligned
                : getX();
        final int textY = textY();

        var text = textToShow();
        var color = colorToShow();

        // Background color
        renderBackground(guiGraphics);

        // Render text and cursor
        withScale(guiGraphics, renderScale, renderScale, () -> {
            String left = text.substring(0, cursorPos);
            String right = text.substring(cursorPos);

            // Render left of cursor
            guiGraphics.drawString(font, left, textX, textY, color, false);
            var rightX = textX + font.width(left); // Where the right of cursor starts

            // Render cursor
            renderCursor(guiGraphics, rightX, textY);

            // Render right of cursor
            guiGraphics.drawString(font, right, rightX, textY, color, false);

            // Render highlight
            renderHighlight(guiGraphics, textX, textY);
        });

        // Render underline
        withScale(guiGraphics, renderScale, 1f, () -> {
            var lineWidth = Math.max(font.width(text), font.width(CURSOR));
            GuiUtils.renderOutline(guiGraphics, textX, getY() + height - 1, lineWidth, 1, color);
        });
    }

    private void renderHighlight(GuiGraphics guiGraphics, int x, int y) {
        if (highlightPos == cursorPos) return;
        int highlightStart = Math.min(cursorPos, highlightPos);
        int highlightEnd = Math.max(cursorPos, highlightPos);
        int highlightX = x + font.width(textToShow().substring(0, highlightStart));
        int highlightWidth = font.width(textToShow().substring(highlightStart, highlightEnd));
        guiGraphics.fill(RenderType.guiOverlay(), highlightX, y, highlightX + highlightWidth, y + font.lineHeight, highlightColor());
    }

    private void renderCursor(GuiGraphics guiGraphics, int x, int y) {
        long time = System.currentTimeMillis();
        var cursorBlinking = time / 300 % 2 == 0; // Blink every 300 ms
        if (!listening() || cursorBlinking) return;
        // Render cursor
        boolean lineCursor = cursorPos < textToShow().length();
        if (lineCursor) {
            // Inline cursor as line
            guiGraphics.fill(RenderType.guiOverlay(), x, y, x + 1, y + font.lineHeight, textColor);
        } else {
            // Underscore cursor
            guiGraphics.drawString(font, CURSOR, x, y, textColor, false);
        }
    }

    private void renderBackground(GuiGraphics guiGraphics) {
        int x = getX();
        int y = getY();
        guiGraphics.fill(x, y, getX() + width, getY() + height, backgroundColor);
    }

    private int textY() {
        // Remove padding for accents, to center visually
        // Actual padding should be 2, but 1 makes it so text is slightly higher as opposed to
        // slightly lower, so looks more aligned this way
        float mainLineHeight = font.lineHeight - 1;
        return (int) (getY() + height / 2f - mainLineHeight * renderScale / 2f);
    }

    private void withScale(GuiGraphics guiGraphics, float scaleX, float scaleY, Runnable runnable) {
        int x = getX();
        int y = getY();
        guiGraphics.pose().pushPose();
        // Move pivot to (x, y)
        guiGraphics.pose().translate(x, y, 0);
        // Scale around that point
        guiGraphics.pose().scale(scaleX, scaleY, 1.0f);
        // Move back so it draws correctly
        guiGraphics.pose().translate(-x, -y, 0);
        // Render
        runnable.run();
        // Pop pose
        guiGraphics.pose().popPose();
    }
}
