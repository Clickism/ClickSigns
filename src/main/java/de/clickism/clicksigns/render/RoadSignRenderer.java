package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.io.IOException;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Road sign renderer
 */
public final class RoadSignRenderer {
    private static final ResourceLocation TEST_TEXTURE = ClickSigns.identifier("textures/block/road_sign.png");
    private static final ResourceLocation TEST_TILESET = ClickSigns.identifier("roadsigns/tileset/white.png");

    private static final float Z_FIGHTING_OFFSET = 0.001f;

    private final RoadSignBlockEntity entity;
    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;
    private final int overlay;

    private final Direction direction;

    public RoadSignRenderer(RoadSignBlockEntity entity, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        this.entity = entity;
        this.stack = stack;
        this.source = source;
        this.light = light;
        this.overlay = overlay;
        this.direction = entity.getBlockState().getValue(HORIZONTAL_FACING);
    }

    public void render() {
        stack.pushPose();
        // Face the direction of the road sign
        faceDirection();

        int blockWidth = 1;
        int blockHeight = 1;
        var texture = texture(blockWidth, blockHeight);
        // Stop rendering if no texture found
        if (texture == null) {
            stack.popPose();
            return;
        }
        // Get image buffer and pose
        var buffer = source.getBuffer(RenderType.entityCutout(texture));
        var pose = stack.last();

        float halfWidth = blockWidth / 2f;
        float halfHeight = blockHeight / 2f;
        // Add quad
        quad(buffer, pose, -halfWidth, -halfHeight, halfWidth, halfHeight);

        stack.popPose();
    }

    private void faceDirection() {
        // Move to center of block
        stack.translate(.5, .5, .5);
        // Rotate around Y axis based on block state direction
        var rotation = (float) Math.toRadians(-this.direction.toYRot());
        stack.mulPose(new Quaternionf().rotateY(rotation));
        // Move back so the sign is flush with the block face, with a small offset to prevent z-fighting
        stack.translate(0, 0, .5 - Z_FIGHTING_OFFSET);
    }

    private @Nullable ResourceLocation texture(int blockWidth, int blockHeight) {
        // TODO: Implement
        try {
            var tiled = new TileSet(TEST_TILESET, 4, 8).generate(blockWidth, blockHeight);
            return Minecraft.getInstance().getTextureManager().register("testroad", tiled);
        } catch (IOException e) {
            return null;
        }
    }

    private void quad(VertexConsumer buffer, PoseStack.Pose pose, float x1, float y1, float x2, float y2) {
        vertex(buffer, pose, x1, y1, 0, 1); // Bottom left
        vertex(buffer, pose, x1, y2, 0, 0); // Top left
        vertex(buffer, pose, x2, y2, 1, 0); // Top right
        vertex(buffer, pose, x2, y1, 1, 1); // Bottom right
    }

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
