package de.clickism.clicksigns.render.element;

import de.clickism.clicksigns.render.RenderContext;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;

/**
 * Interface for rendering sign elements.
 *
 * @param <T> the type of sign element to render
 */
public interface ElementRenderer<T extends SignElement> {
    /**
     * Renders the given element using the provided render context.
     *
     * @param element  the element to render
     * @param context  the render context to use for rendering
     * @param roadSign the road sign that this element belongs to
     */
    void render(T element, RenderContext context, RoadSign roadSign);

    int renderLayer();
}
