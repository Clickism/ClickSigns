package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.util.texture.Texture;

import java.util.List;

/**
 * Road sign class.
 *
 * @param texture  Texture of the roadsign
 * @param elements Elements of the roadsign
 */
public record RoadSign(
        Texture texture,
        List<RoadSignElement> elements
) {
}
