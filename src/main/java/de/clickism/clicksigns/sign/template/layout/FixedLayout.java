package de.clickism.clicksigns.sign.template.layout;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.util.Size;

import java.util.List;

/**
 * A fixed layout has a predefined list of elements and set positions,
 * and only supports one size.
 *
 * @param elements    the list of sign elements in this layout, with fixed positions
 * @param defaultSize the default size for this layout, which is also the only supported size
 */
public record FixedLayout(
        List<SignElement> elements,
        Size defaultSize
) implements Layout {
    @Override
    public List<SignElement> build(Size size) {
        return elements;
    }
}
