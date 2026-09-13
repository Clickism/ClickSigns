package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.ElementProvider;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.elements.input.TextField;
import de.clickism.clickui.layout.Point;
import de.clickism.clickui.layout.Size;
import de.clickism.clickui.render.RenderContext;
import de.clickism.clickui.util.Util;
import net.minecraft.util.Mth;

import static de.clickism.clicksigns.render.element.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * A specialized TextField for editing the text of a SignElement.
 * <p>
 * Handles rendering of the text with scaling and background color, and custom styling.
 */
// TODO: Add support for newlines!
public class SignTextField extends TextField implements ElementProvider {
    private static final int MIN_WIDTH = 4;
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
        // TODO: Translate
        this.placeholder("Text");
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
            Mth.floor(textHeight())
        );
    }

    /**
     * Calculates the current width of the text field based on the text to show and the render scale.
     *
     * @return The calculated width of the text field in pixels.
     */
    protected float currentWidth() {
        float width = elementToShow().width();
        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }
        return width * UI_SCALE;
    }

    /**
     * Returns the TextElement to be shown in the text field, appending the cursor
     * if the field is currently listening for input.
     *
     * @return The TextElement to be displayed in the text field.
     */
    protected TextElement elementToShow() {
        var text = textToShow();
        if (listening()) {
            text += "_";
        }
        return element.withText(text);
    }

    @Override
    public SignElement element() {
        return element.withText(textToShow());
    }

    @Override
    protected int textColor(boolean placeholder) {
        var color = colorResolver.resolveInt(element.style().color());
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
        float offsetY = element.textSize().height() - textOffset.y - Util.font().lineHeight;
        // Add text offset
        x += (int) offsetX;
        y += (int) offsetY;
        return new Point(x, y);
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
        var element = elementToShow();
        var bounds = bounds();
        var background = element.paddedSize();
        var x = bounds.x() + element.backgroundOffset();
        var y = bounds.y() + element.backgroundOffset();
        var color = colorResolver.resolveInt(element.style().backgroundColor().orElseThrow());
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
        var element = elementToShow();
        var bounds = bounds();
        var background = element.paddedSize();
        var thickness = element.style().outlineWidth();
        var color = colorResolver.resolveInt(element.style().outlineColor().orElseThrow());
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
    protected void renderCursor(RenderContext context, int x, int y, boolean inline) {
        super.renderCursor(context, x,
            // Render one above to render on top of underline if not underline
            inline
                ? y
                : y - 1,
            inline);
    }

    @Override
    protected void renderText(RenderContext context, String text, int x, int y, boolean placeholder, String sugestion) {
        var color = textColor(placeholder);
        var graphics = context.graphics();
        var font = context.font();
        // Render the main text
        graphics.drawString(font, text, x, y, color, false); // No shadow
        // Render suggestion
        var suggestionX = x + font.width(text);
        var suggestionColor = UiColor.rgba(color).multiplyAlpha(0.5f).color();
        graphics.drawString(font, sugestion, suggestionX, y, suggestionColor, false); // No shadow

        // Render underline
        var underlineY = y + font.lineHeight - 1;
        var underlineWidth = font.width(text) + font.width(sugestion);
        if (listening()) {
            underlineWidth += font.width("_");
        }
        graphics.fill(x, underlineY, x + underlineWidth, underlineY + 1, color);
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
}
