package de.clickism.clicksigns.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

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

    }
}