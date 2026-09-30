package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import de.clickism.clickui.util.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;

import java.util.function.BiConsumer;

//? if >=26.1 {
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

//~ if >=26.1 'MultiBufferSource' -> 'SubmitNodeCollector' {

import net.minecraft.client.renderer.SubmitNodeCollector;

/**
 * Rnder context for rendering road signs, providing access to the pose stack, buffer source, light level, and direction.
 */
public final class RenderContext {
    private final PoseStack stack;
    private final SubmitNodeCollector source;
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
    public RenderContext(PoseStack stack, SubmitNodeCollector source, int light, Direction direction) {
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
    public SubmitNodeCollector source() {
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
            rotate(Axis.YP, 180);
            stack.translate(-blockWidth / 2, 0, 0);
            action.run();
        });
    }

    public void rotate(Axis axis, float degrees) {
        //? if >=26.3 {
        axis.rotateDegrees(stack.last().pose(), degrees);
        //?} else
        //stack.mulPose(axis.rotationDegrees(degrees));
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
            rotate(Axis.XP, 180);
            rotate(Axis.ZP, 180);
            stack.translate(0, -font.lineHeight, 0);
            action.run();
        });
    }

    public void submit(
        int order,
        RenderType renderType,
        BiConsumer<PoseStack.Pose, VertexConsumer> renderer
    ) {
        //? if >=26.1 {
        source.order(order).submitCustomGeometry(
            stack,
            renderType,
            renderer::accept
        );
        //?} else {
        /*withTranslation(0, 0, order * RenderConstants.ELEMENT_STEP, () -> {
            var buffer = source.getBuffer(renderType);
            renderer.accept(stack.last(), buffer);
        });
        *///?}
    }

    public void submitText(
        int order,
        float x, float y,
        FormattedCharSequence string,
        boolean shadow,
        Font.DisplayMode mode,
        int lightCoords,
        int color,
        int backgroundColor,
        int outlineColor
    ) {
        //? if >=26.1 {
        source.order(order).submitText(
            stack,
            x, y,
            string,
            shadow,
            mode,
            lightCoords,
            color,
            backgroundColor,
            outlineColor
        );
        //?} else {
        /*withTranslation(0, 0, -order * RenderConstants.ELEMENT_STEP, () -> {
            var font = Util.font();
            font.drawInBatch(
                string,
                x, y,
                color,
                shadow,
                stack.last().pose(),
                source,
                mode,
                backgroundColor,
                lightCoords
            );
        });
        *///?}
    }
}
//~}
