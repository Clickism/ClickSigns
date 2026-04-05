package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.clickism.clicksigns.util.Alignment;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

public class TextureRenderer {
    private static final float Z_FIGHTING_OFFSET = 0.001f;

    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;

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
    public void render(Texture texture, int zIndex) {
        render(texture, 0, 0, zIndex);
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
    public void render(Texture texture, float x, float y, int zIndex) {
        render(texture, x, y, zIndex, Alignment.CENTER);
    }

    /**
     * Renders the given texture at the given coordinates (offset from center (0, 0)) and z index.
     * Renders in the center by default.
     * Will align the rendered texture based on the given alignment.
     *
     * @param texture the texture to render
     * @param x       the x offset to translate by (in blocks)
     * @param y       the y offset to translate by (in blocks)
     * @param zIndex  the z index to render at, higher values will render on top
     */
    public void render(Texture texture, float x, float y, int zIndex, Alignment alignment) {
        stack.pushPose();
        // Apply alignment offset
        x -= alignment.offset().x * texture.blockWidth() / 2; // Again, x is flipped so subtract
        y += alignment.offset().y * texture.blockHeight() / 2;
        // Offset by z index to prevent z-fighting
        stack.translate(x, y, -zIndex * Z_FIGHTING_OFFSET);

        var textureLocation = texture.location();
        // Get image buffer and pose
        var buffer = source.getBuffer(RenderType.entityTranslucentCull(textureLocation));
        var pose = stack.last();

        // Calculate width and height for vertex positions
        float halfWidth = texture.blockWidth() / 2f;
        float halfHeight = texture.blockHeight() / 2f;
        // Add quad
        quad(buffer, pose, -halfWidth, -halfHeight, halfWidth, halfHeight);
        // Finish pose
        stack.popPose();
    }

    /**
     * Creates a quad with the given vertex positions
     */
    private void quad(VertexConsumer buffer, PoseStack.Pose pose, float x1, float y1, float x2, float y2) {
        vertex(buffer, pose, x1, y1, 1, 1); // Bottom left
        vertex(buffer, pose, x1, y2, 1, 0); // Top left
        vertex(buffer, pose, x2, y2, 0, 0); // Top right
        vertex(buffer, pose, x2, y1, 0, 1); // Bottom right
    }

    /**
     * Creates a vertex with the given positions and UV coordinates
     */
    private void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v) {
        buffer.vertex(pose.pose(), x, y, 0)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0, 0, 1)
                .endVertex();
    }
}
