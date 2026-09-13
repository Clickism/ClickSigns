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

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXEL;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Road sign renderer
 */
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

    private void renderDebugPoints() {
        // Render at higher Z to avoid z-fighting
        context.withTranslation(0, 0, Z_FIGHTING_OFFSET, () -> {
            // Render red dot at (0,0)
            context.textureRenderer().renderColor(0xFFFF0000, BLOCK_PIXEL, BLOCK_PIXEL);
            // Render blue dot at (width, height)
            context.withTranslation(roadSign.blockWidth(), roadSign.blockHeight(), 0, () -> {
                context.textureRenderer().renderColor(0xFF0000FF, BLOCK_PIXEL, BLOCK_PIXEL);
            });
        });
    }

    private void renderElements() {
        roadSign.elements().forEach(this::renderElement);
    }

    private void renderFront() {
        var frontTexture = roadSign.frontTexture();
        context.textureRenderer().renderTexture(frontTexture);
    }

    private void renderBack() {
        context.withFlip(roadSign.width(), () -> {
            var backTexture = roadSign.backTexture();
            context.textureRenderer().renderTexture(backTexture);
        });
    }

    /**
     * Renders the road sign with its elements and textures.
     */
    public void render() {
        context.stack().pushPose();

        setupTransform();
        renderDebugPoints();

        // Render the front texture of the road sign
        renderFront();

        // Render elements
        renderElements();

        // Render the back texture of the road sign
        renderBack();

        context.stack().popPose();

//        // Render elements
//        roadSign.elements().forEach(element -> {
//            var renderCoords = toRenderCoordinates(frontTexture, element.x(), element.y());
//            var colorResolver = roadSign.colorResolver();
//            // Render based on element type
//            if (element instanceof SymbolElement symbol) {
//                var renderer = new SymbolRenderer();
//                renderer.render(symbol, new SignRenderContext(stack, source, light, roadSign, textureRenderer));
//            } else if (element instanceof TextElement text) {
//                // Render text elements
//                int color = colorResolver.resolveInt(text.style().color());
//                int backgroundColor = text.style().backgroundColor()
//                    .map(colorResolver::resolveInt)
//                    .orElse(0);
////                textRenderer.render(text.text(), color, backgroundColor, text.scale(), renderCoords.x, renderCoords.y, 4, text.alignment());
//            } else if (element instanceof PlateElement plate) {
//                // Render plate elements
//                var texture = plate.front().resolve(roadSign.colorResolver());
//                // Check if the plate is colliding with the road sign texture, if so, render in front the road sign texture
//                var plateRect = new Rectangle((int) plate.alignedX(), (int) plate.alignedY(), texture.width(), texture.height());
//                var colliding = signRect.intersects(plateRect);
//                // Render in front of the road sign texture if colliding
//                var zIndex = colliding
//                    ? 2
//                    : 1;
//                textureRenderer.renderTexture(texture);
//
//                // Render back of plate
//                stack.pushPose();
//                stack.mulPose(FLIP);
//
//                var flippedX = roadSign.width() - element.x(); // Flip X coordinate for back rendering
//                var backCoords = toRenderCoordinates(roadSign.backTexture(), flippedX, element.y());
//                var backTexture = plate.back().resolve(roadSign.colorResolver());
//                // Render behind actual back, incase the back texture is inside the sign bounds
//                var backZIndex = colliding
//                    ? 1
//                    : 2;
//                textureRenderer.renderTexture(backTexture);
//
//                stack.popPose();
//            }
//        });
//
//        // Render back of the road sign
//        stack.mulPose(FLIP);
//        textureRenderer.renderTexture(roadSign.backTexture()); // Render back texture more in front
//
//        // Finish rendering
//        stack.popPose();
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
