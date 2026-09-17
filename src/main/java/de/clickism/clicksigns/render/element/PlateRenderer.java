package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.render.RenderLayers;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Renders a {@link PlateElement} on a {@link RoadSign}.
 */
public class PlateRenderer implements ElementRenderer<PlateElement> {
    @Override
    public void render(PlateElement element, RenderContext context, RoadSign roadSign) {
        var intersects = roadSign.intersects(element);
        int z = intersects
            ? RenderLayers.PLATE_FRONT
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
            ? RenderLayers.PLATE_BACK
            : RenderLayers.SIGN_BACK;
        context.withZ(z, () -> {
            context.withFlip(element.width() / BLOCK_PIXELS, () -> {
                context.textureRenderer().renderTexture(backTexture);
            });
        });
    }

    @Override
    public int renderLayer() {
        return 0; // Handle layer in the render method
    }
}
