package de.clickism.clicksigns.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.render.RoadSignRenderer;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

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
        var roadSign = entity.roadSign();
        if (roadSign == null) {
            roadSign = RoadSign.DEFAULT;
        }
        var direction = entity.getBlockState().getValue(HORIZONTAL_FACING);
        var renderer = new RoadSignRenderer(roadSign, direction, stack, source, light);
        renderer.render();
    }
}