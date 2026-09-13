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
    // TODO: Adjust text scale, so that it's a bit nicer
    //  .022f
    public static final float TEXT_RENDER_SCALE = 3.5f / (9f * BLOCK_PIXELS);
    /**
     * Offset text by 1 pixel so that the actual text is more centered in the line.
     */
    public static final float TEXT_RENDER_OFFSET_Y = 1f;
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
        // Render text
        var text = element.text();
        if (text.isEmpty()) {
            return;
        }

        var font = Util.font();
        var scale = TEXT_RENDER_SCALE * element.scale();
        context.withScale(scale, () -> {
            // Render background
            var style = element.style();
            style.backgroundColor()
                .map(roadSign.colorResolver()::resolveInt)
                .ifPresent(color -> {
                    var textWidth = font.width(text);
                    var textHeight = font.lineHeight;
                    var width = textWidth + style.paddingX() * 2;
                    var height = textHeight + style.paddingY() * 2;
                    var multipliedColor = multiplyColor(color, COLOR_DARKEN_FACTOR); // Darken background color to match texture colors
                    context.withTranslation(-style.paddingX(), -style.paddingY(), 0, () -> {
                        context.textureRenderer().renderColor(
                            multipliedColor,
                            width,
                            height
                        );
                    });
                });
            // Render text
            context.withTextTransform(font, () -> {
                var color = roadSign.colorResolver().resolveInt(style.color());
                // TODO: Find better way to match colors?
                color = multiplyColor(color, COLOR_DARKEN_FACTOR); // Darken text color to match texture colors
                font.drawInBatch(
                    text,
                    0, TEXT_RENDER_OFFSET_Y,
                    color,
                    false,
                    context.stack().last().pose(),
                    context.source(),
                    Font.DisplayMode.POLYGON_OFFSET,
                    0, // No background
                    context.light()
                );
            });
        });
    }

    @Override
    public int renderLayer() {
        return RenderLayers.TEXT;
    }
}
