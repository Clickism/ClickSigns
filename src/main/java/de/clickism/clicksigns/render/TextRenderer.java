package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

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
    private static final float TEXT_PADDING_X = 1f;
    /**
     * Text padding in pixels for the y-axis.
     */
    private static final float TEXT_PADDING_Y = .25f;
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
        stack.pushPose();
        // Calculate dimensions
        float textWidth = font.width(text);
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
}
