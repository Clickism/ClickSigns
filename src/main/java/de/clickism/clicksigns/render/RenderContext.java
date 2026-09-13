package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import de.clickism.clicksigns.util.PixelSized;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;

import static de.clickism.clicksigns.render.RoadSignRenderer.Z_FIGHTING_OFFSET;

public final class RenderContext {
    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;

    private final TextureRenderer textureRenderer;

    public RenderContext(PoseStack stack, MultiBufferSource source, int light) {
        this.stack = stack;
        this.source = source;
        this.light = light;
        this.textureRenderer = new TextureRenderer(this);
    }

    public PoseStack stack() {
        return stack;
    }

    public MultiBufferSource source() {
        return source;
    }

    public int light() {
        return light;
    }

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
     * @param plane  the plane to use
     * @param action the action to execute
     */
    public void withFlip(PixelSized plane, Runnable action) {
        withPose(() -> {
            stack.translate(plane.blockWidth() / 2, 0, 0);
            stack.mulPose(Axis.YP.rotationDegrees(180));
            stack.translate(-plane.blockWidth() / 2, 0, 0);
            action.run();
        });
    }

    public void withScale(float scale, Runnable action) {
        withPose(() -> {
            stack.scale(scale, scale, 1);
            action.run();
        });
    }

    public void withPose(Runnable action) {
        stack.pushPose();
        action.run();
        stack.popPose();
    }

    public void withTextTransform(Font font, Runnable action) {
        stack.pushPose();
        stack.mulPose(Axis.YP.rotationDegrees(180));
        stack.mulPose(Axis.ZP.rotationDegrees(180));
        stack.translate(0, -font.lineHeight, 0);
        action.run();
        stack.popPose();
    }

    public void pushZ(int index) {
        stack.translate(0, 0, index * Z_FIGHTING_OFFSET);
    }
}
