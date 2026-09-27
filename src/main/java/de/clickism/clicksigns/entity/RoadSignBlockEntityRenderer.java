package de.clickism.clicksigns.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.render.RenderContext;
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
    /**
     * Keep the default road sign cached
     */
    private RoadSign defaultRoadSign = null;

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
        var roadSign = roadSignToRender(entity);
        var direction = entity.getBlockState().getValue(HORIZONTAL_FACING);
        var renderer = new RoadSignRenderer(
            new RenderContext(stack, source, light, direction),
            roadSign
        );
        renderer.render();
    }

    /**
     * Returns the road sign to render for the given block entity.
     * If the block entity has no road sign, a default road sign is created and returned.
     *
     * @param entity the block entity to get the road sign from
     * @return the road sign to render
     */
    private RoadSign roadSignToRender(RoadSignBlockEntity entity) {
        var roadSign = entity.roadSign();
        if (roadSign == null) {
            roadSign = getOrCreateDefault();
        }
        return roadSign;
    }

    /**
     * Returns the default road sign, creating it if it does not exist.
     *
     * @return the default road sign
     */
    private RoadSign getOrCreateDefault() {
        if (defaultRoadSign == null) {
            defaultRoadSign = RoadSign.createDefault();
        }
        return defaultRoadSign;
    }
}