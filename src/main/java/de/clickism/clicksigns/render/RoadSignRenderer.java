package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.*;
import de.clickism.clicksigns.sign.texture.generator.ColorReplacer;
import de.clickism.clicksigns.sign.texture.source.StaticTextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clicksigns.util.Alignment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.joml.Vector2f;

import java.util.List;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

/**
 * Road sign renderer
 */
public final class RoadSignRenderer extends Renderer {
    private final Direction direction;
    private final RoadSign roadSign;

    /**
     * Creates a new road sign renderer for the given block entity and rendering context.
     */
    public RoadSignRenderer(RoadSignBlockEntity entity, PoseStack stack, MultiBufferSource source, int light) {
        super(stack, source, light);
        this.direction = entity.getBlockState().getValue(HORIZONTAL_FACING);
        if (entity.roadSign() != null) {
            this.roadSign = entity.roadSign();
        } else {
            this.roadSign = RoadSign.DEFAULT;
        }
    }

    public void render() {
        stack.pushPose();
        // Face the direction of the road sign
        faceDirection();

        var textureRenderer = new TextureRenderer(stack, source, light, direction);
        // Render the road sign texture
        var frontTexture = roadSign.frontTexture();
        textureRenderer.renderTexture(frontTexture, 1);

        var textRenderer = new TextRenderer(stack, source, light, direction);
        roadSign.elements().forEach(element -> {
            var renderCoords = toRenderCoordinates(frontTexture, element.localX(), element.localY());
            // Render element
            var colorResolver = roadSign.colorResolver();
            if (element instanceof SymbolElement symbol) {
                // Render each element on top of the road sign
                var texture = symbol.symbol().texture().resolve(roadSign.colorResolver());
                textureRenderer.renderTexture(texture, renderCoords.x, renderCoords.y, 2, symbol.alignment());
            } else if (element instanceof TextElement text) {
                int color = colorResolver.resolveInt(text.color());
                int backgroundColor = 0;
                if (text.backgroundColor() != null) {
                    backgroundColor = colorResolver.resolveInt(text.backgroundColor());
                }
                textRenderer.render(text.text(), color, backgroundColor, text.scale(), renderCoords.x, renderCoords.y, 3, text.alignment());
            }
        });

        // Render back
        stack.mulPose(FLIP);
        textureRenderer.renderTexture(roadSign.backTexture(), 1);

        // Finish rendering
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
     * Converts local sign coordinates to render coordinates.
     */
    private static Vector2f toRenderCoordinates(Texture texture, float localX, float localY) {
        // Offset by halfWidth and halfHeight, since by default rendered in the center of the texture
        float renderX = localX / BLOCK_PIXELS - texture.blockWidth() / 2;
        float renderY = localY / BLOCK_PIXELS - texture.blockHeight() / 2;
        return new Vector2f(-renderX, renderY);
    }
}
