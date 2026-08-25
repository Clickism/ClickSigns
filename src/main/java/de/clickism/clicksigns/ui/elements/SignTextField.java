package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clickui.elements.input.TextField;

public class SignTextField extends TextField implements ElementProvider {
    private final TextElement element;

    public SignTextField(TextElement element) {
        this.element = element;
    }

    @Override
    public SignElement element() {
        return element;
    }
}
