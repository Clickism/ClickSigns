package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;

/**
 * A utility class for rendering textures and solid colors in a given render context.
 */
public class TextureRenderer {
    private final RenderContext context;

    /**
     * Creates a new texture renderer with the given render context.
     *
     * @param context the render context to use
     */
    public TextureRenderer(RenderContext context) {
        this.context = context;
    }

    /**
     * Renders a texture from (0,0) to (texture.blockWidth(), texture.blockHeight())
     *
     * @param texture the texture to render
     */
    public void renderTexture(Texture texture) {
        var textureLocation = texture.location();
        var buffer = context.source().getBuffer(RenderType.entityTranslucentCull(textureLocation));
        render(buffer, texture.blockWidth(), texture.blockHeight(), 0xFFFFFFFF);
    }

    /**
     * Renders a quad with the given color from (0,0) to (blockWidth, blockHeight)
     *
     * @param color       the color to render the quad with
     * @param blockWidth  the width of the quad in blocks
     * @param blockHeight the height of the quad in blocks
     */
    public void renderColor(int color, float blockWidth, float blockHeight) {
        // TODO: Maybe use the white texture instead?
        var buffer = context.source().getBuffer(RenderType.textBackground());
        render(buffer, blockWidth, blockHeight, color);
    }

    /**
     * Renders an outline with the given color and thickness from (0,0) to (blockWidth, blockHeight)
     *
     * @param color       the color to render the outline with
     * @param blockWidth  the width of the outline in blocks
     * @param blockHeight the height of the outline in blocks
     * @param thickness   the thickness of the outline in blocks
     */
    public void renderOutline(int color, float blockWidth, float blockHeight, float thickness) {
        // Top
        renderColor(color, blockWidth, thickness);
        // Bottom
        context.withTranslation(0, blockHeight - thickness, 0, () -> renderColor(color, blockWidth, thickness));
        // Left
        context.withTranslation(0, 0, 0, () -> renderColor(color, thickness, blockHeight));
        // Right
        context.withTranslation(blockWidth - thickness, 0, 0, () -> renderColor(color, thickness, blockHeight));
    }

    /**
     * Renders a quad with the given texture buffer,
     * from (0,0) to (blockWidth, blockHeight), with the given color.
     */
    private void render(
        VertexConsumer buffer,
        float blockWidth,
        float blockHeight,
        int color
    ) {
        // Add quad
        quad(
            buffer,
            context.stack().last(),
            0, 0,
            blockWidth,
            blockHeight,
            color
        );
    }

    /**
     * Creates a quad with the given vertex positions
     */
    private void quad(VertexConsumer buffer, PoseStack.Pose pose, float x1, float y1, float x2, float y2, int color) {
        vertex(buffer, pose, x1, y1, 0, 1, color); // Bottom left
        vertex(buffer, pose, x2, y1, 1, 1, color); // Bottom right
        vertex(buffer, pose, x2, y2, 1, 0, color); // Top right
        vertex(buffer, pose, x1, y2, 0, 0, color); // Top left
    }

    /**
     * Creates a vertex with the given positions and UV coordinates
     */
    private void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v, int color) {
        var xAxis = context.direction().getAxis() == Direction.Axis.X;
        buffer.vertex(pose.pose(), x, y, 0)
            .color(color)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(context.light())
            // Texture is facing towards -Z
            .normal(pose.normal(), xAxis
                ? 1
                : 0, 0, xAxis
                ? 0
                : 1)
            .endVertex();
    }
}
