package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.awt.*;

/**
 * Texture renderer utility class
 */
public class TextureRenderer extends Renderer {
    private static final Color DEFAULT_COLOR = Color.WHITE;

    private final Direction renderDirection;

    /**
     * Create a new texture renderer with the given rendering context.
     */
    public TextureRenderer(PoseStack stack, MultiBufferSource source, int light, Direction renderDirection) {
        super(stack, source, light);
        this.renderDirection = renderDirection;
    }

    /**
     * Renders the given texture at the center (0, 0) with the given z index.
     *
     * @param texture the texture to render
     * @param zIndex  the z index to render at, higher values will render on top
     */
    public void renderTexture(Texture texture, int zIndex) {
        renderTexture(texture, 0, 0, zIndex, Alignment.CENTER);
    }

    /**
     * Renders the given texture at the given coordinates (offset from center (0, 0)) and z index.
     * Renders in the center by default.
     * Will align the rendered texture based on the given alignment.
     *
     * @param texture   the texture to render
     * @param x         the x offset to translate by (in blocks)
     * @param y         the y offset to translate by (in blocks)
     * @param zIndex    the z index to render at, higher values will render on top
     * @param alignment the alignment to render the texture with
     */
    public void renderTexture(Texture texture, float x, float y, int zIndex, Alignment alignment) {
        var textureLocation = texture.location();
        var buffer = source.getBuffer(RenderType.entityTranslucentCull(textureLocation));
        render(buffer, x, y, texture.blockWidth(), texture.blockHeight(), zIndex, alignment, DEFAULT_COLOR.getRGB());
    }

    /**
     * Renders the given color as a quad at the given coordinates (offset from center (0, 0)) and z index.
     * Renders in the center by default.
     * Will align the rendered quad based on the given alignment.
     *
     * @param color     the color to render
     * @param x         the x offset to translate by (in blocks)
     * @param y         the y offset to translate by (in blocks)
     * @param zIndex    the z index to render at, higher values will render on top
     * @param alignment the alignment to render the quad with
     */
    public void renderColor(int color, float blockWidth, float blockHeight, float x, float y, float zIndex, Alignment alignment) {
        var buffer = source.getBuffer(RenderType.textBackground());
        render(buffer, x, y, blockWidth, blockHeight, zIndex, alignment, color);
    }

    /**
     * Renders a quad with the given texture buffer and coordinates
     */
    private void render(
        VertexConsumer buffer,
        float x, float y,
        float blockWidth,
        float blockHeight,
        float zIndex,
        Alignment alignment,
        int color
    ) {
        stack.pushPose();
        // Apply alignment and z index offset
        align(x, y, blockWidth, blockHeight, zIndex, alignment);
        // Get image buffer and pose
        var pose = stack.last();
        // Calculate width and height for vertex positions
        float halfWidth = blockWidth / 2f;
        float halfHeight = blockHeight / 2f;
        // Add quad
        quad(buffer, pose, -halfWidth, -halfHeight, halfWidth, halfHeight, color);
        // Finish pose
        stack.popPose();
    }

    /**
     * Renders the four edge faces (top, bottom, left, right) that connect a front
     * plane to a back plane, giving a flat texture the appearance of real thickness.
     * <p>
     * The sides are rendered as flat-colored quads (no texture sampling needed),
     * reusing the same solid-color render type as {@link #renderColor}.
     *
     * @param color      the color to render the sides with
     * @param blockWidth the width of the front/back plane (in blocks)
     * @param blockHeight the height of the front/back plane (in blocks)
     * @param thickness  the distance between the front and back planes (in blocks)
     * @param x          the x offset to translate by (in blocks)
     * @param y          the y offset to translate by (in blocks)
     * @param zIndex     the z index the *front* plane renders at; the sides span
     *                   from this z index back by {@code thickness}
     * @param alignment  the alignment to render the sides with
     */
    public void renderSides(int color, float blockWidth, float blockHeight, float thickness, float x, float y, float zIndex, Alignment alignment) {
        var buffer = source.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.tryBuild(
            ResourceLocation.DEFAULT_NAMESPACE,
            "textures/misc/white.png")));
        stack.pushPose();
        // Apply alignment and z index offset, same as a normal quad
        align(x, y, blockWidth, blockHeight, zIndex, alignment);
        var pose = stack.last();

        float halfWidth = blockWidth / 2f;
        float halfHeight = blockHeight / 2f;
        // Front plane sits at local z = 0 (post-align), back plane is `thickness` further away
        float zFront = 0;
        float zBack = thickness;

        // Top edge
        sideQuad(buffer, pose,
            -halfWidth, halfHeight, zFront,
            halfWidth, halfHeight, zFront,
            halfWidth, halfHeight, zBack,
            -halfWidth, halfHeight, zBack,
            0, 1, 0, color);

        // Bottom edge
        sideQuad(buffer, pose,
            -halfWidth, -halfHeight, zBack,
            halfWidth, -halfHeight, zBack,
            halfWidth, -halfHeight, zFront,
            -halfWidth, -halfHeight, zFront,
            0, -1, 0, color);

        // Left edge
        sideQuad(buffer, pose,
            -halfWidth, -halfHeight, zBack,
            -halfWidth, -halfHeight, zFront,
            -halfWidth,  halfHeight, zFront,
            -halfWidth,  halfHeight, zBack,
            -1, 0, 0, color);

        // Right edge
        sideQuad(buffer, pose,
            halfWidth, -halfHeight, zFront,
            halfWidth, -halfHeight, zBack,
            halfWidth,  halfHeight, zBack,
            halfWidth,  halfHeight, zFront,
            1, 0, 0, color);

        stack.popPose();
    }

    /**
     * Creates a quad with the given vertex positions
     */
    private void quad(VertexConsumer buffer, PoseStack.Pose pose, float x1, float y1, float x2, float y2, int color) {
        vertex(buffer, pose, x1, y1, 1, 1, color); // Bottom left
        vertex(buffer, pose, x1, y2, 1, 0, color); // Top left
        vertex(buffer, pose, x2, y2, 0, 0, color); // Top right
        vertex(buffer, pose, x2, y1, 0, 1, color); // Bottom right
    }

    /**
     * Creates a quad for a side face with explicit 3D vertex positions and a fixed normal,
     * instead of the flat-panel positions/UVs used by {@link #quad}.
     */
    private void sideQuad(
        VertexConsumer buffer, PoseStack.Pose pose,
        float x0, float y0, float z0,
        float x1, float y1, float z1,
        float x2, float y2, float z2,
        float x3, float y3, float z3,
        float nx, float ny, float nz,
        int color
    ) {
        vertex(buffer, pose, x0, y0, z0, 0, 1, nx, ny, nz, color);
        vertex(buffer, pose, x1, y1, z1, 1, 1, nx, ny, nz, color);
        vertex(buffer, pose, x2, y2, z2, 1, 0, nx, ny, nz, color);
        vertex(buffer, pose, x3, y3, z3, 0, 0, nx, ny, nz, color);
    }

    /**
     * Creates a vertex with the given positions and UV coordinates
     */
    private void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v, int color) {
        var isXAxis = renderDirection.getAxis() == Direction.Axis.X;
        buffer.vertex(pose.pose(), x, y, 0)
            .color(color)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(light)
            .normal(pose.normal(), isXAxis
                ? 1
                : 0, 0, isXAxis
                ? 0
                : 1)
            .endVertex();
    }

    /**
     * Creates a vertex at an explicit 3D local position with an explicit normal.
     * Used for side faces, which (unlike flat front/back quads) have real depth
     * and a normal that varies per-face instead of always facing the render direction.
     */
    private void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v, float nx, float ny, float nz, int color) {
        buffer.vertex(pose.pose(), x, y, z)
            .color(color)
            .uv(u, v)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(light)
            .normal(pose.normal(), nx, ny, nz)
            .endVertex();
    }
}