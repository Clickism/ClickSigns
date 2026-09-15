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
        // Render front
        if (!roadSign.intersects(element)) {
            // Push Z back to align with the front of the sign
            context.pushZ(RenderLayers.SIGN_FRONT - renderLayer());
        }
        var frontTexture = element.frontSource().resolve(roadSign.colorResolver());
        context.textureRenderer().renderTexture(frontTexture);
        // Render back
        var backTexture = element.backSource().resolve(roadSign.colorResolver());
        // If not intersecting with the road sign, align with the front, so that there is not a gap inbetween
        // TODO: Fix z offset when intersecting
        context.withFlip(element.width() / BLOCK_PIXELS, () -> {
            context.textureRenderer().renderTexture(backTexture);
        });
    }

    @Override
    public int renderLayer() {
        return RenderLayers.PLATE_FRONT;
    }
}
