package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.render.RenderLayers;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clickui.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FastColor;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

public class TextRenderer implements ElementRenderer<TextElement> {
    // TODO: Adjust text scale, so that it's a bit nicer?
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
        int a = FastColor.ARGB32.alpha(color);
        int r = (int) (FastColor.ARGB32.red(color) * factor);
        int g = (int) (FastColor.ARGB32.green(color) * factor);
        int b = (int) (FastColor.ARGB32.blue(color) * factor);
        // Keep within bounds just in case
        r = r & 0xFF;
        g = g & 0xFF;
        b = b & 0xFF;
        return FastColor.ARGB32.color(a, r, g, b);
    }

    @Override
    public void render(TextElement element, RenderContext context, RoadSign roadSign) {
        var text = element.text();
        if (text.isEmpty()) {
            return;
        }
        var scale = TEXT_RENDER_SCALE * element.scale();
        // Apply scale
        context.withScale(scale, () -> {
            // Render background and outline
            renderStyle(element, context, roadSign);
            // Render text
            var textPos = element.textOffset();
            context.withTranslation(textPos.x, textPos.y, 0, () -> {
                // Render text
                renderText(element, context, roadSign);
            });
        });
    }

    @Override
    public int renderLayer() {
        return RenderLayers.TEXT;
    }

    /**
     * Renders the text of the text element.
     *
     * @param element  the text element to render
     * @param context  the render context
     * @param roadSign the road sign being rendered
     */
    private void renderText(TextElement element, RenderContext context, RoadSign roadSign) {
        var color = roadSign.colorResolver().resolveInt(element.style().color());
        // TODO: Find better way to match colors?
        var font = Util.font();
        context.withTextTransform(font, () -> {
            font.drawInBatch(
                element.text(),
                0, 0,
                multiplyColor(color, COLOR_DARKEN_FACTOR),
                false,
                context.stack().last().pose(),
                context.source(),
                Font.DisplayMode.POLYGON_OFFSET,
                0, // No background
                context.light()
            );
        });
    }

    /**
     * Renders the background and outline of the text element.
     *
     * @param element  the text element to render
     * @param context  the render context
     * @param roadSign the road sign being rendered
     */
    private void renderStyle(TextElement element, RenderContext context, RoadSign roadSign) {
        var style = element.style();
        var background = element.paddedSize();
        var outlineWidth = style.isOutlineShown() ? style.outlineWidth() : 0;
        // Render background
        style.backgroundColor()
            .map(roadSign.colorResolver()::resolveInt)
            .ifPresent(color -> {
                context.withTranslation(outlineWidth, outlineWidth, 0, () -> {
                    context.textureRenderer().renderColor(
                        multiplyColor(color, COLOR_DARKEN_FACTOR),
                        background.width(),
                        background.height()
                    );
                });
            });

        // Render outline
        style.outlineColor()
            .map(roadSign.colorResolver()::resolveInt)
            .ifPresent(color -> {
                var thickness = style.outlineWidth();
                if (thickness <= 0) return;
                // Darken background color to match texture colors
                context.textureRenderer().renderOutline(
                    multiplyColor(color, COLOR_DARKEN_FACTOR),
                    background.width() + thickness * 2,
                    background.height() + thickness * 2,
                    thickness
                );
            });
    }
}
