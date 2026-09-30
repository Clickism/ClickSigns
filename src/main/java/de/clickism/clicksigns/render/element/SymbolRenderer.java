package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;

/**
 * Renders a {@link SymbolElement} on a {@link RoadSign}.
 */
public class SymbolRenderer implements ElementRenderer<SymbolElement> {
    @Override
    public void render(SymbolElement element, RenderContext context, RoadSign roadSign) {
        int order = orderOf(element, roadSign);
        var texture = element.textureSource().resolve(roadSign.colorResolver());
        context.textureRenderer().renderElementTexture(order, texture, element.scale());
    }
}
