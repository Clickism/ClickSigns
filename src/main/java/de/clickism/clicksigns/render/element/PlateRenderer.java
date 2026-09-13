package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.render.RenderLayers;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;

public class PlateRenderer implements ElementRenderer<PlateElement> {
    @Override
    public void render(PlateElement element, RenderContext context, RoadSign roadSign) {
        // TODO: Varying z index based on whether or not colliding with road sign
        // Render front
        var frontTexture = element.front().resolve(roadSign.colorResolver());
        context.textureRenderer().renderTexture(frontTexture);
        // Render back
        // TODO: Is this even correct?
        var backTexture = element.back().resolve(roadSign.colorResolver());
        context.withFlip(element.width(), () -> {
            context.textureRenderer().renderTexture(backTexture);
        });
    }

    @Override
    public int renderLayer() {
        return RenderLayers.PLATE_FRONT;
    }
}
