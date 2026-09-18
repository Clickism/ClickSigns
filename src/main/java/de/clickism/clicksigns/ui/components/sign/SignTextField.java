package de.clickism.clicksigns.ui.components.sign;

import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.elements.input.TextField;
import de.clickism.clickui.layout.Point;
import de.clickism.clickui.layout.Size;
import de.clickism.clickui.render.RenderContext;
import de.clickism.clickui.util.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import static de.clickism.clicksigns.render.element.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.t;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * A specialized TextField for editing the text of a SignElement.
 * <p>
 * Handles rendering of the text with scaling and background color, and custom styling.
 */
public class SignTextField extends TextField implements ElementProvider {
    private TextElement element;
    private ColorResolver colorResolver;

    /**
     * Creates a new SignTextField for the given TextElement and ColorResolver.
     *
     * @param element       The TextElement to display and edit in this text field.
     * @param colorResolver The ColorResolver to use for resolving colors for the text and background.
     */
    public SignTextField(TextElement element, ColorResolver colorResolver) {
        this.element = element;
        this.colorResolver = colorResolver;
        this.scrolling(false);
        this.placeholder(t("clicksigns.textPlaceholder").getString());
        this.value(element.text());
        // Set up style
        this.overrideStyle(style()
            .backgroundColor(null));
        // Set up listeners so that width is recalculated when needed
        this.onValueChanged(value -> {
            this.invalidateLayout();
        });
        this.onFocusEnter(event -> {
            this.invalidateLayout();
        });
        this.onFocusExit(event -> {
            this.invalidateLayout();
        });
        // Set up padding
        this.padding(0);
        this.multiLine(true);
    }

    public SignTextField textElement(TextElement element) {
        this.element = element;
        this.invalidateLayout();
        return this;
    }

    public SignTextField colorResolver(ColorResolver colorResolver) {
        this.colorResolver = colorResolver;
        return this;
    }

    @Override
    public Size intrinsicSize() {
        return new Size(
            Mth.floor(currentWidth()),
            Mth.floor(currentHeight())
        );
    }

    /**
     * Calculates the current width of the text field based on the text to show and the render scale.
     *
     * @return The calculated width of the text field in pixels.
     */
    protected float currentWidth() {
        float width = elementToLayout().width();
        return width * UI_SCALE;
    }

    /**
     * Calculates the current height of the text field based on the text to show and the render scale.
     *
     * @return The calculated height of the text field in pixels.
     */
    protected float currentHeight() {
        var height = elementToLayout().height();
        return height * UI_SCALE;
    }

    /**
     * Returns the text element that should be used for layout calculations.
     *
     * @return the text element to use for layout
     */
    protected TextElement elementToLayout() {
        var text = textToShow();
        return element.withText(text);
    }

    /**
     * Returns the text element that will be rendered in the text field
     *
     * @return the text element to render
     */
    protected TextElement elementToShow() {
        return element.withText(textToShow());
    }

    @Override
    public SignElement element() {
        return element.withText(textToShow());
    }

    /**
     * Moves the cursor vertically by the specified delta (number of lines).
     *
     * @param delta The number of lines to move the cursor. Positive values move down, negative values move up.
     */
    protected void moveCursorVertically(int delta) {
        var linePos = linePosOf(cursorPos);
        var lineText = elementToShow().lines();
        int newLine = Mth.clamp(linePos.line + delta, 0, lineText.size() - 1);
        // Calculate new position in the line visually, based on the x position of the cursor in the current line
        var font = Util.font();
        int cursorX = font.width(lineText.get(linePos.line).substring(0, linePos.pos)) + element.lineXOffset(lineText.get(linePos.line));
        int newPos = 0;
        for (int i = 0; i < lineText.get(newLine).length(); i++) {
            int charWidth = font.width(lineText.get(newLine).substring(i, i + 1));
            int charX = font.width(lineText.get(newLine).substring(0, i)) + element.lineXOffset(lineText.get(newLine));
            if (charX + charWidth / 2 >= cursorX) {
                newPos = i;
                break;
            } else {
                newPos = lineText.get(newLine).length();
            }
        }
        cursorPos = new LinePos(newLine, newPos).toCursorPos();
        if (!Screen.hasShiftDown()) {
            highlightPos = cursorPos;
        }
    }

    @Override
    protected boolean handleKeyPress(int code) {
        if (code == GLFW.GLFW_KEY_ENTER || code == GLFW.GLFW_KEY_KP_ENTER) {
            // Insert newline
            insertText("\n");
            return true;
        }
        if (code == GLFW.GLFW_KEY_UP) {
            // Move cursor up a line
            moveCursorVertically(-1);
            return true;
        }
        if (code == GLFW.GLFW_KEY_DOWN) {
            // Move cursor down a line
            moveCursorVertically(1);
            return true;
        }
        return super.handleKeyPress(code);
    }

    @Override
    protected int textColor(boolean placeholder) {
        var color = colorResolver.resolve(element.style().color());
        if (placeholder) {
            color = UiColor.rgba(color).multiplyAlpha(0.5f).color();
        }
        return color;
    }

    @Override
    protected float textHeight() {
        return element.height() * UI_SCALE;
    }

    @Override
    protected Point textPosition() {
        var bounds = bounds();
        var x = bounds.x();
        var y = bounds.y();
        // Apply text offset
        var textOffset = element.textOffset();
        float offsetX = textOffset.x;
        // Flip y to convert to UI space
        float offsetY = element.totalSize().height() - textOffset.y - element.unpaddedSize().height();
        // Add text offset
        x += (int) offsetX;
        y += (int) offsetY;
        return new Point(x, y);
    }

    @Override
    protected int cursorPosAt(int mouseX, int mouseY) {
        var scale = renderScale();
        var bounds = bounds();
        // Undo the render transform around the field's top-left corner.
        float localMouseX = bounds.x() + (mouseX - bounds.x()) / scale;
        float localMouseY = bounds.y() + (mouseY - bounds.y()) / scale;
        var textPos = textPosition();
        int x = Mth.ceil(localMouseX - textPos.x());
        int y = Mth.ceil(localMouseY - textPos.y());
        // Calculate line based on y position
        var lines = elementToShow().lines();
        int lineHeight = Util.font().lineHeight + element.style().lineGap();
        // Keep index within bounds
        int lineIndex = Mth.clamp(y / lineHeight, 0, lines.size() - 1);
        // Calculate character index based on x position
        var lineText = lines.get(lineIndex);
        x -= element.lineXOffset(lineText); // Adjust x based on line offset
        int charIndex = Util.font().plainSubstrByWidth(lineText, x).length();
        // Calculate cursor position
        int lineCursorPos = 0;
        for (int i = 0; i < lineIndex; i++) {
            lineCursorPos += lines.get(i).length() + 1; // +1 for newline
        }
        return lineCursorPos + charIndex;
    }

    @Override
    public void render(RenderContext context) {
        // Render the text field
        var bounds = bounds();
        // Apply render scale
        var renderScale = renderScale();
        renderWithScale(context, bounds.x(), bounds.y(), renderScale, renderScale, () -> {
            // Render background
            renderBackground(context);
            // Render outline
            renderOutline(context);
            // Render text and highlight
            super.render(context);
        });
    }

    /**
     * Renders the background of the text field based on the style of the associated TextElement.
     *
     * @param context The render context used for rendering.
     */
    private void renderBackground(RenderContext context) {
        if (!element.style().isBackgroundShown()) {
            return;
        }
        var element = elementToLayout();
        var bounds = bounds();
        var background = element.paddedSize();
        var x = bounds.x() + element.backgroundOffset();
        var y = bounds.y() + element.backgroundOffset();
        var color = colorResolver.resolve(element.style().backgroundColor().orElseThrow());
        // Fill background
        context.graphics().fill(
            x,
            y,
            x + background.width(),
            y + background.height(),
            color
        );
    }

    /**
     * Renders the outline of the text field if the outline is enabled in the style.
     *
     * @param context The render context used for rendering.
     */
    private void renderOutline(RenderContext context) {
        if (!element.style().isOutlineShown()) {
            return;
        }
        var element = elementToLayout();
        var bounds = bounds();
        var background = element.paddedSize();
        var thickness = element.style().outlineWidth();
        var color = colorResolver.resolve(element.style().outlineColor().orElseThrow());
        // Render outline
        UiUtil.renderOutline(
            context.graphics(),
            bounds.x(),
            bounds.y(),
            background.width() + thickness * 2,
            background.height() + thickness * 2,
            thickness,
            color
        );
    }

    @Override
    protected void renderHighlight(RenderContext context, int x, int y, int width) {
        // Render based on lines
        var lines = elementToShow().lines();
        var highlightStart = linePosOf(highlightStart());
        var highlightEnd = linePosOf(highlightEnd());
        // Render each line of the highlight
        for (int lineIndex = highlightStart.line; lineIndex <= highlightEnd.line; lineIndex++) {
            var line = lines.get(lineIndex);
            int startChar = (lineIndex == highlightStart.line)
                ? highlightStart.pos
                : 0;
            int endChar = (lineIndex == highlightEnd.line)
                ? highlightEnd.pos
                : line.length();
            var font = context.font();
            var lineText = line.substring(startChar, endChar);
            var textPos = textPosition();
            int highlightX = textPos.x() + font.width(line.substring(0, startChar)) + element.lineXOffset(line);
            int highlightY = textPos.y() + lineIndex * font.lineHeight + (lineIndex * element.style().lineGap());
            int highlightWidth = font.width(lineText);
            super.renderHighlight(context, highlightX, highlightY, highlightWidth);
        }
    }

    @Override
    protected void renderCursor(RenderContext context, int x, int y, boolean inline) {
        // Calculate position based on lines
        var element = elementToShow();
        var lines = element.lines();
        var linePos = linePosOf(cursorPos);
        var lineIndex = linePos.line;
        var charIndex = linePos.pos;
        // Calculate x position based on character width
        var font = context.font();
        var line = lines.get(lineIndex);
        var lineText = line.substring(0, Math.min(charIndex, line.length()));
        var textPos = textPosition();
        // Calculate the position of the cursor
        x = textPos.x() + font.width(lineText) + element.lineXOffset(line);
        y = textPos.y() + lineIndex * font.lineHeight + (lineIndex * this.element.style().lineGap());
        context.graphics().pose().pushPose();
        context.graphics().pose().translate(0, 0, 100); // Move cursor to front
        super.renderCursor(context, x,
            // Render one above to render on top of underline if not underline
            inline
                ? y
                : y - 1,
            inline);
        context.graphics().pose().popPose();
    }

    @Override
    protected void renderText(RenderContext context, String text, int x, int y, boolean placeholder, String suggestion) {
        var color = textColor(placeholder);
        var graphics = context.graphics();
        var font = context.font();
        // Render the main text
        var element = elementToShow();
        var lines = element.lines();
        var lineY = y;
        for (var line : lines) {
            var lineX = x + element.lineXOffset(line);
            graphics.drawString(font, line, lineX, lineY, color, false); // No shadow
            lineY += font.lineHeight + this.element.style().lineGap();
        }
        // No suggestion support
    }

    /**
     * Renders the given runnable with the specified scale applied.
     *
     * @param context  the render context
     * @param cornerX  the x-coordinate of the pivot point for scaling
     * @param cornerY  the y-coordinate of the pivot point for scaling
     * @param scaleX   the scale factor in the x direction
     * @param scaleY   the scale factor in the y direction
     * @param runnable the rendering code to execute
     */
    private void renderWithScale(RenderContext context, int cornerX, int cornerY, float scaleX, float scaleY, Runnable runnable) {
        var graphics = context.graphics();
        graphics.pose().pushPose();
        // Move pivot to (x, y)
        graphics.pose().translate(cornerX, cornerY, 0);
        // Scale around that point
        graphics.pose().scale(scaleX, scaleY, 1.0f);
        // Move back so it draws correctly
        graphics.pose().translate(-cornerX, -cornerY, 0);
        // Render
        runnable.run();
        // Pop pose
        graphics.pose().popPose();
    }

    /**
     * Calculates the render scale for the text field based on the block pixels,
     * text render scale, element scale, and UI scale.
     *
     * @return The calculated render scale.
     */
    private float renderScale() {
        return BLOCK_PIXELS
               * TEXT_RENDER_SCALE
               * element.scale()
               * UI_SCALE;
    }

    /**
     * Converts a cursor position in the text to a line and position within that line.
     *
     * @param cursorPos The cursor position in the text.
     * @return A LinePos object containing the line index and position within that line.
     */
    private LinePos linePosOf(int cursorPos) {
        var lines = elementToShow().lines();
        if (lines.isEmpty()) {
            return new LinePos(0, 0);
        }
        int charCount = 0;
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (cursorPos <= charCount + line.length()) {
                return new LinePos(i, cursorPos - charCount);
            }
            charCount += line.length() + 1; // +1 for newline
        }
        int lastLine = lines.size() - 1;
        return new LinePos(lastLine, lines.get(lastLine).length());
    }

    /**
     * A helper class representing a position in the text as a line index and a character position within that line.
     */
    protected class LinePos {
        protected final int line;
        protected final int pos;

        /**
         * Constructs a new LinePos object.
         *
         * @param line The index of the line.
         * @param pos  The position within the line.
         */
        protected LinePos(int line, int pos) {
            this.line = line;
            this.pos = pos;
        }

        /**
         * Converts the LinePos to a cursor position in the text.
         *
         * @return The cursor position corresponding to this LinePos.
         */
        protected int toCursorPos() {
            var lines = elementToShow().lines();
            int cursorPos = 0;
            for (int i = 0; i < line; i++) {
                cursorPos += lines.get(i).length() + 1; // +1 for newline
            }
            cursorPos += pos;
            return cursorPos;
        }
    }
}
