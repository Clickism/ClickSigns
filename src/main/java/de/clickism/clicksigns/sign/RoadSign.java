package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.util.texture.Texture;

import java.util.List;

/**
 * @param texture Texture of the roadsign
 * @param width   Width in blocks
 * @param height  Height in blocks
 * @param symbols Symbols on the roadsign
 */
// TODO: Maybe generalize elements more?
public record RoadSign(
        Texture texture,
        List<SymbolElement> symbols
) {

}
