package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.sign.Alignment;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Quaternionf;

/**
 * Utility class for common rendering logic
 */
public abstract class Renderer {
    /**
     * The offset to apply to the Z coordinate when rendering to prevent z-fighting
     */
    protected static final float Z_FIGHTING_OFFSET = 0.001f;

    protected static final Quaternionf FLIP = new Quaternionf().rotateY((float) (Math.PI));

    protected final PoseStack stack;
    protected final MultiBufferSource source;
    protected final int light;

    /**
     * Creates a new aligned renderer with the given rendering context.
     */
    public Renderer(PoseStack stack, MultiBufferSource source, int light) {
        this.stack = stack;
        this.source = source;
        this.light = light;
    }

    /**
     * Aligns the texture based on the given coordinates and alignment.
     */
    protected void align(float x, float y, float blockWidth, float blockHeight, float zIndex, Alignment alignment) {
        // Apply alignment offset
        x -= alignment.offset().x * blockWidth / 2; // Again, x is flipped so subtract
        y += alignment.offset().y * blockHeight / 2;
        // Offset by z index to prevent z-fighting
        stack.translate(x, y, -zIndex * Z_FIGHTING_OFFSET);
    }

    /**
     * Aligns the texture based on the given coordinates and alignment.
     * If alignment is center, will render centered on the block.
     * Otherwise, will align with current alignment, but cover the whole block,
     * instead of rendering from the center of the block.
     *
     * @param x           the x offset to translate by (in blocks)
     * @param y           the y offset to translate by (in blocks)
     * @param blockWidth  the width of the block to render on (in blocks)
     * @param blockHeight the height of the block to render on (in blocks)
     * @param zIndex      the z index to render at, higher values will render on top
     * @param alignment   the alignment to render the texture with
     */
    protected void alignFromBlockCenter(float x, float y, float blockWidth, float blockHeight, float zIndex, Alignment alignment) {
        // Apply alignment offset
        x -= alignment.offset().x * blockWidth / 2; // Again, x is flipped so subtract
        y += alignment.offset().y * blockHeight / 2;
        // Move opposite of alignment offset by half a block to center on block
        x += alignment.offset().x * .5f;
        y -= alignment.offset().y * .5f;
        // Offset by z index to prevent z-fighting
        stack.translate(x, y, -zIndex * Z_FIGHTING_OFFSET);
    }
}
