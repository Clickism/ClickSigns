package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.awt.*;

import static de.clickism.clicksigns.util.Constants.Z_FIGHTING_OFFSET;

/**
 * Text renderer utility class
 */
public class TextRenderer {
    private static final float TEXT_RENDER_SCALE = .022f;
    private static final float TEXT_PADDING_X = .1f;
    private static final float TEXT_PADDING_Y = .1f;

    private static final Quaternionf FLIP = new Quaternionf().rotateY((float) (Math.PI));

    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;

    private final Font font;

    /**
     * Create a new text renderer with the given rendering context.
     */
    public TextRenderer(
            PoseStack stack,
            MultiBufferSource source,
            int light
    ) {
        this.stack = stack;
        this.source = source;
        this.light = light;
        // Use client font
        this.font = Minecraft.getInstance().font;
    }

    /**
     * Renders the given text at the center (0, 0) with the given z index and color.
     *
     * @param text   the text to render
     * @param color  the color to render the text in
     * @param x      the x offset to translate by (in blocks)
     * @param y      the y offset to translate by (in blocks)
     * @param zIndex the z index to render at, higher values will render on top
     */
    public void render(
            String text,
            Color color,
            @Nullable Color backgroundColor,
            float textScale,
            float x,
            float y,
            int zIndex,
            Alignment alignment
    ) {
        stack.pushPose();
        // Offset by z index to prevent z-fighting
        stack.translate(x, y, -zIndex * Z_FIGHTING_OFFSET);
        // Render background if given
        if (backgroundColor != null) {
            renderBackground(text, backgroundColor, textScale, x, y, alignment);
        }
        // Scale text
        float scale = TEXT_RENDER_SCALE * textScale;
        stack.scale(scale, -scale, scale);
        // Rotate text to face the player
        stack.mulPose(FLIP);
        // Calculate offset, by default renders at bottom right
        float textWidth = font.width(text);
        float textHeight = font.lineHeight;
        // Offset to center
        float textX = -textWidth / 2f;
        float textY = -textHeight / 2f;
        // Apply alignment offset from center (in text coordinates)
        textX += alignment.offset().x * textWidth / 2;
        textY -= alignment.offset().y * textHeight / 2; // y is flipped so subtract
        // Draw text
        font.drawInBatch(
                text,
                // Apply text offset
                textX, textY,
                // Apply color
                color.getRGB(),
                false,
                stack.last().pose(),
                source,
                Font.DisplayMode.NORMAL,
                0,
                light
        );
        // Finish rendering
        stack.popPose();
    }

    /**
     * Renders a background rectangle with the given color behind the text,
     * with padding based on the text size.
     */
    private void renderBackground(String text, Color backgroundColor, float scale, float x, float y, Alignment alignment) {
        float textWidth = font.width(text);
        float textHeight = font.lineHeight;
        float blockWidth = textWidth * TEXT_RENDER_SCALE * scale;
        float blockHeight = textHeight * TEXT_RENDER_SCALE * scale;
        // Apply padding
        float paddingX = blockWidth * TEXT_PADDING_X;
        float paddingY = blockHeight * TEXT_PADDING_Y;
        blockWidth += paddingX * 2;
        blockHeight += paddingY * 2;
        // Apply offset to account for padding
        x += alignment.offset().x * paddingX;
        y -= alignment.offset().y * paddingY; // y is flipped so subtract
        // Adjust y to visually center
        y += paddingY / 2;
        // Render background
        var textureRenderer = new TextureRenderer(stack, source, light);
        textureRenderer.renderColor(backgroundColor, blockWidth, blockHeight, x, y, -1, alignment);
    }
}
