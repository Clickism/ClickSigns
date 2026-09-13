package de.clickism.clicksigns.render;

import com.mojang.math.Axis;
import de.clickism.clicksigns.render.element.ElementRenderer;
import de.clickism.clicksigns.render.element.PlateRenderer;
import de.clickism.clicksigns.render.element.SymbolRenderer;
import de.clickism.clicksigns.render.element.TextRenderer;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Road sign renderer
 */
// TODO: Fix normals/lighting issues. Some directions are darker than others which looks off
public final class RoadSignRenderer {
    // TODO: Move to render layers or constants
    public static final float Z_FIGHTING_OFFSET = 0.001f;

    private final Direction direction;
    private final RoadSign roadSign;

    private final RenderContext context;

    /**
     * Creates a new road sign renderer for the given block entity and rendering context.
     */
    public RoadSignRenderer(
        RenderContext context,
        @NotNull RoadSign roadSign,
        Direction direction
    ) {
        this.context = context;
        this.direction = direction;
        this.roadSign = roadSign;
    }

    private void setupTransform() {
        var stack = context.stack();
        // Face the direction of the road sign
        stack.translate(.5, .5, .5); // Rotate from the center of the block
        // Rotate 180 to base around "north", so that X goes right and Y goes up
        stack.mulPose(Axis.YP.rotationDegrees(180));
        // Rotate to match the direction of the road sign
        stack.mulPose(Axis.YP.rotationDegrees(-direction.toYRot()));
        stack.translate(-.5, -.5, -.5);

        // Here, (0,0) is bottom left, and (width, height) is top right of the block

        // Align road sign based on its alignment

        // Alignment assumes center, so first center on block
        float extraWidth = roadSign.blockWidth() - 1;
        float extraHeight = roadSign.blockHeight() - 1;
        stack.translate(-extraWidth / 2, -extraHeight / 2, 0);

        // Apply alignment offset
        var alignment = roadSign.alignment();
        float offsetX = alignment.offset().x * roadSign.blockWidth() / 2;
        float offsetY = alignment.offset().y * roadSign.blockHeight() / 2;

        // To make sure the sign covers the whole block, move back by half alignment offset
        // TODO: Does not work if height/width < 1
        offsetX -= alignment.offset().x / 2;
        offsetY -= alignment.offset().y / 2;

        // Align the sign based on its alignment offset
        stack.translate(offsetX, offsetY, 0);
    }

    /**
     * Renders all elements of the road sign.
     */
    private void renderElements() {
        roadSign.elements().forEach(this::renderElement);
    }

    /**
     * Renders the front texture of the road sign.
     */
    private void renderFront() {
        var frontTexture = roadSign.frontTexture();
        context.textureRenderer().renderTexture(frontTexture);
    }

    /**
     * Renders the back texture of the road sign.
     */
    private void renderBack() {
        context.withFlip(roadSign.blockWidth(), () -> {
            var backTexture = roadSign.backTexture();
            context.textureRenderer().renderTexture(backTexture);
        });
    }

    /**
     * Renders the road sign with its elements and textures.
     */
    public void render() {
        context.stack().pushPose();
        // Set up the transformatiosn for the road sign based on its direction and alignment
        setupTransform();
        context.pushZ(RenderLayers.SIGN_FRONT);

        // Render the front texture of the road sign
        renderFront();
        // Render elements
        renderElements();
        // Render the back texture of the road sign
        renderBack();

        // End pose
        context.stack().popPose();
    }


    /**
     * Renders a sign element using its corresponding renderer.
     *
     * @param element the sign element to render
     * @param <T>     the type of the sign element, which must extend SignElement
     */
    private <T extends SignElement> void renderElement(T element) {
        var renderer = rendererForElement(element);
        var x = element.alignedX() / BLOCK_PIXELS;
        var y = element.alignedY() / BLOCK_PIXELS;
        var z = renderer.renderLayer() * Z_FIGHTING_OFFSET;
        context.withTranslation(x, y, z, () -> {
            renderer.render(element, context, roadSign);
        });
    }

    /**
     * Returns the renderer for a given sign element based on its type.
     *
     * @param element the sign element for which to retrieve the renderer
     * @param <T>     the type of the sign element, which must extend SignElement
     * @return the renderer corresponding to the type of the sign element
     */
    @SuppressWarnings("unchecked")
    private <T extends SignElement> ElementRenderer<T> rendererForElement(T element) {
        return (ElementRenderer<T>) switch (element.typeKey()) {
            case TextElement.TYPE -> new TextRenderer();
            case SymbolElement.TYPE -> new SymbolRenderer();
            case PlateElement.TYPE -> new PlateRenderer();
            default -> throw new IllegalArgumentException("No renderer found for element type: " + element.typeKey());
        };
    }
}
