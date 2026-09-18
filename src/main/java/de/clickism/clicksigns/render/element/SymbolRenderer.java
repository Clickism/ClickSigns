package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.render.RenderLayers;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;

/**
 * Renders a {@link SymbolElement} on a {@link RoadSign}.
 */
public class SymbolRenderer implements ElementRenderer<SymbolElement> {
    @Override
    public void render(SymbolElement element, RenderContext context, RoadSign roadSign) {
        var texture = element.textureSource().resolve(roadSign.colorResolver());
        context.textureRenderer().renderTexture(texture);
    }

    @Override
    public int renderLayer() {
        return RenderLayers.SYMBOL;
    }
}
