package de.clickism.clicksigns.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

/**
 * Renderer for road signs
 */
public class RoadSignBlockEntityRenderer implements BlockEntityRenderer<RoadSignBlockEntity> {
    @Override
    public void render(
            RoadSignBlockEntity blockEntity,
            float tickDelta,
            PoseStack poseStack,
            MultiBufferSource multiBufferSource,
            int light,
            int overlay
    ) {

    }
}
