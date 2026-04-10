package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * Text renderer utility class
 */
public class TextRenderer extends Renderer {
    /**
     * The scale multiplier used to render text
     */
    public static final float TEXT_RENDER_SCALE = .022f;

    private static final float TEXT_PADDING_X = .1f;
    private static final float TEXT_PADDING_Y = .1f;
    private final Font font;
    private final TextureRenderer textureRenderer;

    /**
     * Create a new text renderer with the given rendering context.
     */
    public TextRenderer(PoseStack stack, MultiBufferSource source, int light) {
        super(stack, source, light);
        // Use client font
        this.font = Minecraft.getInstance().font;
        this.textureRenderer = new TextureRenderer(stack, source, light);
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
        stack.pushPose();
        // Calculate dimensions
        float textWidth = font.width(text);
        float textHeight = font.lineHeight;
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
        // Draw text
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
        // Apply padding
        float paddingX = blockWidth * TEXT_PADDING_X;
        float paddingY = blockHeight * TEXT_PADDING_Y;
        blockWidth += paddingX * 2;
        blockHeight += paddingY * 2;

        float x = 0;
        float y = paddingY / 2; // Adjust y to visually center
        // Render background
        textureRenderer.renderColor(backgroundColor, blockWidth, blockHeight, x, y, -1, Alignment.CENTER);
    }
}
