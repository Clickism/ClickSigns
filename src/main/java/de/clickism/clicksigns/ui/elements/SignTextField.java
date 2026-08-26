package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clickui.elements.input.TextField;
import de.clickism.clickui.layout.Size;
import de.clickism.clickui.render.RenderContext;
import de.clickism.clickui.style.Style;
import net.minecraft.util.Mth;

import java.awt.*;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

// TODO: Refactor and come up with clear rendering guidelines for sign elements and text element outline, border, etc.

/**
 * A specialized TextField for editing the text of a SignElement.
 * <p>
 * Handles rendering of the text with scaling and background color, and custom styling.
 */
public class SignTextField extends TextField implements ElementProvider {
    private static final int MIN_WIDTH = 4;
    private static final int BACKGROUND_PADDING = 3;

    private final TextElement element;
    private final ColorResolver colorResolver;

    // TODO: Refactor renderScale?
    private final float renderScale;

    /**
     * Creates a new SignTextField for the given TextElement and ColorResolver.
     *
     * @param element       The TextElement to display and edit in this text field.
     * @param colorResolver The ColorResolver to use for resolving colors for the text and background.
     */
    public SignTextField(TextElement element, ColorResolver colorResolver) {
        this.element = element;
        this.colorResolver = colorResolver;
        this.renderScale = BLOCK_PIXELS
                           * TEXT_RENDER_SCALE
                           * element.scale()
                           * DEFAULT_TEXTURE_RENDER_SCALE;
        this.scrolling(false);
        // TODO: Translate
        this.placeholder("Text");
        this.value(element.text());
        // Set up style
        var background = element.backgroundColor() == null
            ? null
            : colorResolver.resolve(element.backgroundColor());
        this.overrideStyle(Style.empty()
            .whenHovered(s -> s
                .border(new Color(GuiUtils.OUTLINE_COLOR)))
            .background(background));
        // Set up listeners so that width is recalculated when needed
        this.onValueChanged(value -> {
            this.invalidate();
        });
        this.onFocusEnter(event -> {
            this.invalidate();
        });
        this.onFocusExit(event -> {
            this.invalidate();
        });
        // Set up height
        var font = GuiUtils.font();
        this.height(Mth.ceil(font.lineHeight * renderScale) + 2);
        // Set up padding
        int padding = 0;
        if (element.backgroundColor() != null) {
            padding = Mth.ceil(BACKGROUND_PADDING * element.scale());
        }
        this.padding(1, padding, 0, padding);
    }

    @Override
    public Size intrinsicSize() {
        return new Size(
            currentWidth(),
            Mth.ceil(textHeight() + 2)
        );
    }

    /**
     * Calculates the current width of the text field based on the text to show and the render scale.
     *
     * @return The calculated width of the text field in pixels.
     */
    protected int currentWidth() {
        var text = textToShow();
        var font = GuiUtils.font();
        float width = font.width(text);
        if (listening()) {
            width += font.width("_");
        }
        width *= renderScale;
        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }
        return Mth.ceil(width);
    }

    @Override
    public SignElement element() {
        return element.withText(value());
    }

    @Override
    protected int textColor(boolean placeholder) {
        var color = colorResolver.resolveInt(element.color());
        if (placeholder) {
            color = GuiUtils.colorWithMultipliedAlpha(color, 0.5f);
        }
        return color;
    }

    @Override
    protected float textHeight() {
        return GuiUtils.font().lineHeight * renderScale;
    }

    @Override
    public void render(RenderContext context) {
        // Render background
//        renderBackground(context);
        // Render the text field
        var pos = textPosition();
        renderWithScale(context, pos.x(), pos.y(), renderScale, renderScale, () -> {
            super.render(context);
        });
    }

    private void renderBackground(RenderContext context) {
        if (element.backgroundColor() == null) return;
        var background = colorResolver.resolveInt(element.backgroundColor());
        var graphics = context.graphics();
        var bounds = bounds();
        // Render background with the resolved color
        graphics.fill(
            bounds.x(), bounds.y(),
            bounds.x() + bounds.width(), bounds.y() + bounds.height(),
            background
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
        // Render the main text
        var font = context.font();
        graphics.drawString(font, text, x, y, color, false); // No shadow
        // Render suggestion
        var suggestionX = x + font.width(text);
        var suggestionColor = GuiUtils.colorWithMultipliedAlpha(color, 0.5f);
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
}
