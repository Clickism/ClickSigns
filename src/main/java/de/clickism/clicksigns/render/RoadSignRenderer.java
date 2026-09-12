package de.clickism.clicksigns.render;

import com.mojang.blaze3d.vertex.PoseStack;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clickui.UiColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.awt.*;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Road sign renderer
 */
public final class RoadSignRenderer extends Renderer {
    private final Direction direction;
    private final RoadSign roadSign;

    /**
     * Creates a new road sign renderer for the given block entity and rendering context.
     */
    public RoadSignRenderer(
        @NotNull RoadSign roadSign,
        Direction direction,
        PoseStack stack,
        MultiBufferSource source,
        int light
    ) {
        super(stack, source, light);
        this.direction = direction;
        this.roadSign = roadSign;
    }

    /**
     * Converts local sign coordinates to render coordinates.
     * <p>
     * Here, the origin of the sign (0, 0) is at the center of the sign, and the coordinates are in pixels.
     */
    private static Vec2 toRenderCoordinates(Texture texture, float localX, float localY) {
        // Offset by halfWidth and halfHeight, since by default rendered in the center of the texture
        float renderX = localX / BLOCK_PIXELS - texture.blockWidth() / 2;
        float renderY = localY / BLOCK_PIXELS - texture.blockHeight() / 2;
        // Flip x coordinate, becuase x is flipped because of the direction matrix
        return new Vec2(-renderX, renderY);
    }

    /**
     * Renders the road sign with its elements and textures.
     */
    public void render() {
        stack.pushPose();
        // Face the direction of the road sign
        faceDirection();

        var textureRenderer = new TextureRenderer(stack, source, light, direction);
        var frontTexture = roadSign.frontTexture();
        // Align according to the road sign's alignment
        alignFromBlockCenter(0, 0, frontTexture.blockWidth(), frontTexture.blockHeight(), 0, roadSign.alignment());
        // Render the road sign texture
        textureRenderer.renderTexture(frontTexture, 1);
        // Render the side edges connecting front and back
        int sideColor = UiColor.GRAY.color();
        var thickness = .5f / BLOCK_PIXELS; // 1 pixel thickness

        var buffer = source.getBuffer(RenderType.entityCutoutNoCull(ResourceLocation.tryBuild(
            ResourceLocation.DEFAULT_NAMESPACE,
            "textures/misc/white.png")));
        SilhouetteExtruder.renderSilhouetteSides(
            buffer, stack.last(), light,
            frontTexture.location(), frontTexture.blockWidth(), frontTexture.blockHeight(),
            thickness, sideColor
        );

//        textureRenderer.renderSides(
//            sideColor,
//            frontTexture.blockWidth(),
//            frontTexture.blockHeight(),
//            thickness,
//            0, 0,
//            1,
//            Alignment.CENTER // Since already applied
//        );

        var textRenderer = new TextRenderer(stack, source, light, direction);
        var signRect = new Rectangle(0, 0, frontTexture.width(), frontTexture.height());
        // Render elements
        roadSign.elements().forEach(element -> {
            var renderCoords = toRenderCoordinates(frontTexture, element.x(), element.y());
            var colorResolver = roadSign.colorResolver();
            // Render based on element type
            if (element instanceof SymbolElement symbol) {
                var texture = symbol.symbol().texture().resolve(roadSign.colorResolver());
                textureRenderer.renderTexture(texture, renderCoords.x, renderCoords.y, 3, symbol.alignment());
            } else if (element instanceof TextElement text) {
                // Render text elements
                int color = colorResolver.resolveInt(text.color());
                int backgroundColor = 0;
                if (text.backgroundColor() != null) {
                    backgroundColor = colorResolver.resolveInt(text.backgroundColor());
                }
                textRenderer.render(text.text(), color, backgroundColor, text.scale(), renderCoords.x, renderCoords.y, 4, text.alignment());
            } else if (element instanceof PlateElement plate) {
                // Render plate elements
                var texture = plate.front().resolve(roadSign.colorResolver());
                // Check if the plate is colliding with the road sign texture, if so, render in front the road sign texture
                var plateRect = new Rectangle((int) plate.alignedX(), (int) plate.alignedY(), texture.width(), texture.height());
                var colliding = signRect.intersects(plateRect);
                // Render in front of the road sign texture if colliding
                var zIndex = colliding
                    ? 2
                    : 1;
                textureRenderer.renderTexture(texture, renderCoords.x, renderCoords.y, zIndex, plate.alignment());
                textureRenderer.renderSides(
                    sideColor,
                    texture.blockWidth(),
                    texture.blockHeight(),
                    thickness,
                    renderCoords.x, renderCoords.y,
                    zIndex,
                    plate.alignment() // Since already applied
                );

                // Render back of plate
                stack.pushPose();
                stack.mulPose(FLIP);
                stack.translate(0, 0, -thickness); // Move back to the back plane

                var flippedX = roadSign.width() - element.x(); // Flip X coordinate for back rendering
                var backCoords = toRenderCoordinates(roadSign.backTexture(), flippedX, element.y());
                var backTexture = plate.back().resolve(roadSign.colorResolver());
                // Render behind actual back, incase the back texture is inside the sign bounds
                var backZIndex = colliding
                    ? -2
                    : -1;
                textureRenderer.renderTexture(backTexture, backCoords.x, backCoords.y, backZIndex, plate.alignment());

                stack.popPose();
            }
        });

        // Render back of the road sign
        stack.mulPose(FLIP);
        stack.translate(0, 0, -thickness); // Move back to the back plane
        textureRenderer.renderTexture(roadSign.backTexture(), -1); // Render back texture more in front

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
}
