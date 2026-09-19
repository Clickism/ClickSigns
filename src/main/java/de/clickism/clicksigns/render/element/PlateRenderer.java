package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.render.RenderLayers;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Renders a {@link PlateElement} on a {@link RoadSign}.
 */
public class PlateRenderer implements ElementRenderer<PlateElement> {
    /**
     * Checks if the road sign's main texture intersects with the given sign element.
     *
     * @param roadSign the road sign to check for intersection
     * @param element  the sign element to check for intersection
     * @return true if the road sign intersects with the sign element, false otherwise
     */
    private static boolean intersects(RoadSign roadSign, SignElement element) {
        var left = element.alignedX();
        var top = element.alignedY();
        var right = left + element.width();
        var bottom = top + element.height();
        return left < roadSign.width()
               && right > 0
               && top < roadSign.height()
               && bottom > 0;
    }

    @Override
    public void render(PlateElement element, RenderContext context, RoadSign roadSign) {
        var intersects = intersects(roadSign, element);
        int index = roadSign.elements().indexOf(element);
        int z = intersects
            ? index
            : RenderLayers.SIGN_FRONT;
        context.withZ(z, () -> {
            // Render front
            var frontTexture = element.frontSource().resolve(roadSign.colorResolver());
            context.textureRenderer().renderTexture(frontTexture);
        });
        // Render back
        var masked = RoadSign.maskedBackOf(element.frontSource(), element.backSource());
        var backTexture = masked.resolve(roadSign.colorResolver());
        // If not intersecting with the road sign, align with the front, so that there is not a gap inbetween
        z = intersects
            ? RenderLayers.PLATE_BACK - index
            : RenderLayers.SIGN_BACK;
        context.withZ(z, () -> {
            context.withFlip(element.width() / BLOCK_PIXELS, () -> {
                context.textureRenderer().renderTexture(backTexture);
            });
        });
    }

    @Override
    public int zIndexOf(PlateElement element, RoadSign roadSign) {
        return 0;
    }
}
