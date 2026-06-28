package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.sign.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Text renderer utility class
 */
public class TextRenderer extends Renderer {
    /**
     * The scale multiplier used to render text
     */
    public static final float TEXT_RENDER_SCALE = .022f;

    /**
     * Text padding in pixels for the x-axis.
     */
    public static final float TEXT_PADDING_X = 1f;
    /**
     * Text padding in pixels for the y-axis.
     */
    public static final float TEXT_PADDING_Y = .25f;
    private final Font font;
    private final TextureRenderer textureRenderer;

    /**
     * Create a new text renderer with the given rendering context.
     */
    public TextRenderer(PoseStack stack, MultiBufferSource source, int light, Direction renderDirection) {
        super(stack, source, light);
        // Use client font
        this.font = Minecraft.getInstance().font;
        this.textureRenderer = new TextureRenderer(stack, source, light, renderDirection);
    }

    /**
     * Renders the given text at the center (0, 0) with the given z index and color.
     *
     * @param text            the text to render
     * @param color           the color to render the text in
     * @param backgroundColor the color to render the text background in, or 0 for no background
     * @param x               the x offset to translate by (in blocks)
     * @param y               the y offset to translate by (in blocks)
     * @param zIndex          the z index to render at, higher values will render on top
     */
    public void render(
            String text,
            int color,
            int backgroundColor,
            float textScale,
            float x,
            float y,
            int zIndex,
            Alignment alignment
    ) {
        if (text.isEmpty()) return;
        stack.pushPose();
        // Calculate dimensions
        float textWidth = font.width(text) - 1; // Subtract one, because by default there is 1 empty pixel to the right
        float textHeight = font.lineHeight; // TODO: Check if correct with scaling?
        float blockWidth = textWidth * TEXT_RENDER_SCALE * textScale;
        float blockHeight = textHeight * TEXT_RENDER_SCALE * textScale;
        // Align text
        // Use higher z index because background will subtract one later
        align(x, y, blockWidth, blockHeight, zIndex + 1, alignment);
        // Render background if given
        if (backgroundColor != 0) {
            renderBackground(backgroundColor, blockWidth, blockHeight);
        }
        // Scale text
        float scale = TEXT_RENDER_SCALE * textScale;
        stack.scale(scale, -scale, scale);
        // Rotate text to face the player
        stack.mulPose(FLIP);
        // Offset to center
        float textX = -textWidth / 2f;
        float textY = -textHeight / 2f;
        // Center withing padded background
        float paddingX = backgroundColor != 0 ? TEXT_PADDING_X / BLOCK_PIXELS : 0;
        textX += paddingX / TEXT_RENDER_SCALE;
        // Draw text
        color = multiplyColor(color, 0.8f); // Darken color to match texture colors
        font.drawInBatch(
                text,
                // Apply text offset
                textX, textY,
                // Apply color
                color,
                false, // No shadow
                stack.last().pose(),
                source,
                Font.DisplayMode.NORMAL,
                0, // No background color
                light
        );
        // Finish rendering
        stack.popPose();
    }

    /**
     * Renders a background rectangle with the given color behind the text,
     * with padding based on the text size.
     */
    private void renderBackground(int backgroundColor, float blockWidth, float blockHeight) {
        float x = 0;
        float y = 0;
        // TODO: Maybe multiply with scale?
        float paddingX = TEXT_PADDING_X / BLOCK_PIXELS;
        float paddingY = TEXT_PADDING_Y / BLOCK_PIXELS;
        // Visually center with padding
        x -= paddingX;
        y += paddingY;
        // Add padding
        blockWidth += paddingX * 2;
        blockHeight += paddingY * 2;
        // Render background
        textureRenderer.renderColor(backgroundColor, blockWidth, blockHeight, x, y, -1, Alignment.CENTER);
    }

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
        r = Mth.clamp(r, 0, 0xFF);
        g = Mth.clamp(g, 0, 0xFF);
        b = Mth.clamp(b, 0, 0xFF);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
