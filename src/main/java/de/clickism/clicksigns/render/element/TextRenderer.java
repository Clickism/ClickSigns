package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clickui.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

import static de.clickism.clicksigns.util.ColorUtil.ARGB.*;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Renders a {@link TextElement} on a {@link RoadSign}.
 */
public class TextRenderer implements ElementRenderer<TextElement> {
    public static final float TEXT_RENDER_SCALE = 3.5f / (9f * BLOCK_PIXELS);
    private static final float COLOR_DARKEN_FACTOR = 0.74f;

    /**
     * Multiplies the RGB components of the given ARGB color
     *
     * @param color  the ARGB color to multiply
     * @param factor the factor to multiply the RGB components by
     * @return the resulting ARGB color with the same alpha and multiplied RGB components
     */
    private static int multiplyColor(int color, float factor) {
        int a = alpha(color);
        int r = (int) (red(color) * factor);
        int g = (int) (green(color) * factor);
        int b = (int) (blue(color) * factor);
        // Keep within bounds just in case
        r = r & 0xFF;
        g = g & 0xFF;
        b = b & 0xFF;
        return argb(a, r, g, b);
    }

    @Override
    public void render(TextElement element, RenderContext context, RoadSign roadSign) {
        if (element.text().isEmpty()) return;
        int order = orderOf(element, roadSign);
        var scale = TEXT_RENDER_SCALE * element.scale();
        // Apply scale
        context.withScale(scale, () -> {
            // Render background and outline
            renderStyle(order, element, context, roadSign);
            // Render text
            var textPos = element.textOffset();
            context.withTranslation(textPos.x, textPos.y, 0, () -> {
                // Render text
                renderText(order + 1, element, context, roadSign);
            });
        });
    }

    /**
     * Renders the text of the text element.
     *
     * @param element  the text element to render
     * @param context  the render context
     * @param roadSign the road sign being rendered
     */
    private void renderText(int order, TextElement element, RenderContext context, RoadSign roadSign) {
        var style = element.style();
        var componentStyle = style.asComponentStyle();
        var color = roadSign.colorResolver().resolve(style.color());
        var font = Util.font();
        context.withTextTransform(font, () -> {
            int offsetY = 0;
            // Render in reverse, so that the first line is on top
            for (int i = element.lines().size() - 1; i >= 0; i--) {
                var line = element.lines().get(i);
                int offsetX = element.lineXOffset(line);
                var formatted = FormattedCharSequence.forward(line, componentStyle);
                context.submitText(
                    order,
                    offsetX, -offsetY,
                    formatted,
                    false,
                    Font.DisplayMode.POLYGON_OFFSET,
                    context.light(),
                    multiplyColor(color, COLOR_DARKEN_FACTOR),
                    0,
                    0 // No outline
                );
                offsetY += font.lineHeight + style.lineGap();
            }
        });
    }

    /**
     * Renders the background and outline of the text element.
     *
     * @param element  the text element to render
     * @param context  the render context
     * @param roadSign the road sign being rendered
     */
    private void renderStyle(int order, TextElement element, RenderContext context, RoadSign roadSign) {
        var style = element.style();
        var background = element.paddedSize();
        var outlineWidth = style.isOutlineShown()
            ? style.outlineWidth()
            : 0;
        // Render background
        style.backgroundColor()
            .map(roadSign.colorResolver()::resolve)
            .ifPresent(color -> {
                context.withTranslation(outlineWidth, outlineWidth, 0, () -> {
                    context.textureRenderer().renderColor(
                        order,
                        color,
                        background.width(),
                        background.height()
                    );
                });
            });

        // Render outline
        style.outlineColor()
            .map(roadSign.colorResolver()::resolve)
            .ifPresent(color -> {
                var thickness = style.outlineWidth();
                if (thickness <= 0) return;
                // Darken background color to match texture colors
                context.textureRenderer().renderOutline(
                    order,
                    color,
                    background.width() + thickness * 2,
                    background.height() + thickness * 2,
                    thickness
                );
            });
    }
}
