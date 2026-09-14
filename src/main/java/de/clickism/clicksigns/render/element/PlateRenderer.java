package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.render.RenderLayers;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

public class PlateRenderer implements ElementRenderer<PlateElement> {
    @Override
    public void render(PlateElement element, RenderContext context, RoadSign roadSign) {
        // TODO: Varying z index based on whether or not colliding with road sign
        // Render front
        var frontTexture = element.frontSource().resolve(roadSign.colorResolver());
        context.textureRenderer().renderTexture(frontTexture);
        // Render back
        var backTexture = element.backSource().resolve(roadSign.colorResolver());
        context.withFlip(element.width() / BLOCK_PIXELS, () -> {
            context.textureRenderer().renderTexture(backTexture);
        });
    }

    @Override
    public int renderLayer() {
        return RenderLayers.PLATE_FRONT;
    }
}
