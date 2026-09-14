package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;

import static de.clickism.clicksigns.render.RoadSignRenderer.Z_FIGHTING_OFFSET;

/**
 * Rnder context for rendering road signs, providing access to the pose stack, buffer source, light level, and direction.
 */
public final class RenderContext {
    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;
    private final Direction direction;

    private final TextureRenderer textureRenderer;

    /**
     * Creates a new render context with the given pose stack, buffer source, light level, and direction.
     *
     * @param stack     the pose stack to use for rendering
     * @param source    the buffer source to use for rendering
     * @param light     the light level to use for rendering
     * @param direction the direction to render in
     */
    public RenderContext(PoseStack stack, MultiBufferSource source, int light, Direction direction) {
        this.stack = stack;
        this.source = source;
        this.light = light;
        this.direction = direction;
        this.textureRenderer = new TextureRenderer(this);
    }

    /**
     * Returns the pose stack used for rendering.
     *
     * @return the pose stack
     */
    public PoseStack stack() {
        return stack;
    }

    /**
     * Returns the buffer source used for rendering.
     *
     * @return the buffer source
     */
    public MultiBufferSource source() {
        return source;
    }

    /**
     * Returns the light level used for rendering.
     *
     * @return the light level
     */
    public int light() {
        return light;
    }

    /**
     * Returns the direction to render in.
     *
     * @return the direction
     */
    public Direction direction() {
        return direction;
    }

    /**
     * Returns the texture renderer used for rendering textures.
     *
     * @return the texture renderer
     */
    public TextureRenderer textureRenderer() {
        return textureRenderer;
    }

    /**
     * Executes the given action with the translations applied.
     *
     * @param x      the translation along the X-axis
     * @param y      the translation along the Y-axis
     * @param z      the translation along the Z-axis
     * @param action the action to execute
     */
    public void withTranslation(float x, float y, float z, Runnable action) {
        stack.translate(x, y, z);
        action.run();
        stack.translate(-x, -y, -z);
    }

    /**
     * Executes the given action, where the pose stack is rotated 180 deg,
     * around the center of the given plane.
     *
     * @param blockWidth the width of the plane in blocks
     * @param action     the action to execute
     */
    public void withFlip(float blockWidth, Runnable action) {
        withPose(() -> {
            stack.translate(blockWidth / 2, 0, 0);
            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.translate(-blockWidth / 2, 0, 0);
            action.run();
        });
    }

    /**
     * Executes the given action with the scale applied.
     *
     * @param scale  the scale to apply
     * @param action the action to execute
     */
    public void withScale(float scale, Runnable action) {
        withPose(() -> {
            stack.scale(scale, scale, 1);
            action.run();
        });
    }

    /**
     * Executes the given action with a new pose pushed onto the stack.
     *
     * @param action the action to execute
     */
    public void withPose(Runnable action) {
        stack.pushPose();
        action.run();
        stack.popPose();
    }

    /**
     * Executes the given action with the text transform applied.
     *
     * @param font   the font to use for the text transform
     * @param action the action to execute
     */
    public void withTextTransform(Font font, Runnable action) {
        withPose(() -> {
            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.mulPose(Axis.ZP.rotationDegrees(180));
            stack.translate(0, -font.lineHeight, 0);
            action.run();
        });
    }

    /**
     * Pushes the pose stack along the Z-axis to prevent z-fighting.
     *
     * @param index the index of the Z-fighting offset to apply
     */
    public void pushZ(int index) {
        stack.translate(0, 0, index * Z_FIGHTING_OFFSET);
    }
}
