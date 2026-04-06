package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

import java.awt.*;

import static de.clickism.clicksigns.util.Constants.Z_FIGHTING_OFFSET;

/**
 * Texture renderer utility class
 */
public class TextureRenderer {
    private final Color DEFAULT_COLOR = Color.WHITE;

    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;

    /**
     * Create a new texture renderer with the given rendering context.
     */
    public TextureRenderer(
            PoseStack stack,
            MultiBufferSource source,
            int light
    ) {
        this.stack = stack;
        this.source = source;
        this.light = light;
    }

    /**
     * Renders the given texture at the center (0, 0) with the given z index.
     *
     * @param texture the texture to render
     * @param zIndex  the z index to render at, higher values will render on top
     */
    public void renderTexture(Texture texture, int zIndex) {
        renderTexture(texture, 0, 0, zIndex);
    }

    /**
     * Renders the given texture at the given coordinates (offset from center (0, 0)) and z index.
     * Will align the rendered texture in the center by default.
     *
     * @param texture the texture to render
     * @param x       the x offset to translate by (in blocks)
     * @param y       the y offset to translate by (in blocks)
     * @param zIndex  the z index to render at, higher values will render on top
     */
    public void renderTexture(Texture texture, float x, float y, int zIndex) {
        renderTexture(texture, x, y, zIndex, Alignment.CENTER);
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
        render(buffer, x, y, texture.blockWidth(), texture.blockHeight(), zIndex, alignment, DEFAULT_COLOR);
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
    public void renderColor(Color color, float blockWidth, float blockHeight, float x, float y, float zIndex, Alignment alignment) {
        var buffer = source.getBuffer(RenderType.gui());
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
            Color color
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
     * Aligns the texture based on the given coordinates and alignment.
     */
    private void align(float x, float y, float blockWIdth, float blockHeight, float zIndex, Alignment alignment) {
        // Apply alignment offset
        x -= alignment.offset().x * blockWIdth / 2; // Again, x is flipped so subtract
        y += alignment.offset().y * blockHeight / 2;
        // Offset by z index to prevent z-fighting
        stack.translate(x, y, -zIndex * Z_FIGHTING_OFFSET);
    }

    /**
     * Creates a quad with the given vertex positions
     */
    private void quad(VertexConsumer buffer, PoseStack.Pose pose, float x1, float y1, float x2, float y2, Color color) {
        vertex(buffer, pose, x1, y1, 1, 1, color); // Bottom left
        vertex(buffer, pose, x1, y2, 1, 0, color); // Top left
        vertex(buffer, pose, x2, y2, 0, 0, color); // Top right
        vertex(buffer, pose, x2, y1, 0, 1, color); // Bottom right
    }

    /**
     * Creates a vertex with the given positions and UV coordinates
     */
    private void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v, Color color) {
        buffer.vertex(pose.pose(), x, y, 0)
                .color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha())
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0, 0, 1)
                .endVertex();
    }
}
