package de.clickism.clicksigns.sign.template.layout;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.util.Size;

import java.util.List;

public record FixedLayout(
        List<SignElement> elements
) implements Layout {
    @Override
    public List<SignElement> build(Size size) {
        return elements;
    }
}
