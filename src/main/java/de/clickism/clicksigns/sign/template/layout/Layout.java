package de.clickism.clicksigns.sign.template.layout;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.util.Size;

import java.util.List;

public interface Layout {
    List<SignElement> build(Size size);

    Size defaultSize();
}
