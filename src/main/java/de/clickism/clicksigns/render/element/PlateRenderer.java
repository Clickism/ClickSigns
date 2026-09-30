package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderConstants;
import de.clickism.clicksigns.render.RenderContext;
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
        boolean onSign = intersects(roadSign, element);
        // Base order for plates hanging off the sign, so they use the block-safe pipeline
        int order = onSign ? orderOf(element, roadSign) : RenderConstants.BASE_ORDER;

        // Front
        var frontTexture = element.frontSource().resolve(roadSign.colorResolver());
        context.textureRenderer().renderBaseTexture(order, frontTexture, 1f);

        // Back
        var masked = RoadSign.maskedBackOf(element.frontSource(), element.backSource());
        var backTexture = masked.resolve(roadSign.colorResolver());
        context.withFlip(element.width() / BLOCK_PIXELS, () ->
        context.textureRenderer().renderBaseTexture(order, backTexture, 1f));
    }

    @Override
    public float zOf(PlateElement element, RoadSign roadSign) {
        return intersects(roadSign, element)
            ? ElementRenderer.super.zOf(element, roadSign)
            : RenderConstants.SURFACE_OFFSET;
    }
}
