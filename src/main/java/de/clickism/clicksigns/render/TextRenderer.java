package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.util.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Quaternionf;

import java.awt.*;

import static de.clickism.clicksigns.util.Constants.Z_FIGHTING_OFFSET;

/**
 * Text renderer utility class
 */
public class TextRenderer {
    private static final float TEXT_RENDER_SCALE = .022f;

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
    // TODO: Add background color support
    public void render(String text, Color color, float textScale, float x, float y, int zIndex, Alignment alignment) {
        stack.pushPose();
        // Offset by z index to prevent z-fighting
        stack.translate(x, y, -zIndex * Z_FIGHTING_OFFSET);
        // Scale text
        float scale = TEXT_RENDER_SCALE * textScale;
        stack.scale(scale, -scale, scale);
        // Rotate text to face the player
        stack.mulPose(new Quaternionf().rotateY((float) (Math.PI)));

        // Calculate offset, by default renders at bottom right
        float textWidth = font.width(text);
        float textHeight = font.lineHeight;
        // Offset to center
        float textX = -textWidth / 2f;
        float textY = -textHeight / 2f;
        // Apply alignment offset from center
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
}
