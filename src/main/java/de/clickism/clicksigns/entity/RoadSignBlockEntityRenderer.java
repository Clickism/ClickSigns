package de.clickism.clicksigns.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.entity.render.RoadSignRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * Renderer for road signs
 */
public class RoadSignBlockEntityRenderer implements BlockEntityRenderer<RoadSignBlockEntity> {

    public RoadSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(
            RoadSignBlockEntity entity,
            float tickDelta,
            PoseStack stack,
            MultiBufferSource source,
            int light,
            int overlay
    ) {
        // Use road sign renderer
        var renderer = new RoadSignRenderer(entity, stack, source, light, overlay);
        renderer.render();
    }
}