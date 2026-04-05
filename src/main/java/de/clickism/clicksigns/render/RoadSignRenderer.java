package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Road sign renderer
 */
public final class RoadSignRenderer {
    private final RoadSignBlockEntity entity;
    private final PoseStack stack;
    private final MultiBufferSource source;
    private final int light;
    private final int overlay;

    private final Direction direction;
    private final RoadSign roadSign;

    /**
     * Creates a new road sign renderer for the given block entity and rendering context.
     */
    public RoadSignRenderer(RoadSignBlockEntity entity, PoseStack stack, MultiBufferSource source, int light, int overlay) {
        this.entity = entity;
        this.stack = stack;
        this.source = source;
        this.light = light;
        this.overlay = overlay;
        this.direction = entity.getBlockState().getValue(HORIZONTAL_FACING);
        this.roadSign = entity.roadSign();
    }

    public void render() {
        stack.pushPose();
        // Face the direction of the road sign
        faceDirection();

        var textureRenderer = new TextureRenderer(stack, source, light);
        // Render the road sign texture
        textureRenderer.render(roadSign.texture(), 1);

        roadSign.symbols().forEach(symbol -> {
            // Render each symbol on top of the road sign
            var renderCoords = toRenderCoordinates(roadSign.texture(), 0, 0);
            textureRenderer.render(symbol.texture(), renderCoords.x, renderCoords.y, 2);
        });

        stack.popPose();
    }

    private void faceDirection() {
        // Move to center of block
        stack.translate(.5, .5, .5);
        // Rotate around Y axis based on block state direction
        var rotation = (float) Math.toRadians(-this.direction.toYRot());
        stack.mulPose(new Quaternionf().rotateY(rotation));
        // Move back so the sign is flush with the block face
        stack.translate(0, 0, .5);
    }

    /**
     * Converts local texture coordinates to render coordinates.
     */
    private static Vector2f toRenderCoordinates(Texture texture, float localX, float localY) {
        // Offset by halfWidth and halfHeight, since by default rendered in the center of the texture
        float renderX = localX / BLOCK_PIXELS + texture.blockWidth() / 2; // For some reason X is flipped
        float renderY = localY / BLOCK_PIXELS - texture.blockHeight() / 2;
        return new Vector2f(renderX, renderY);
    }
}
