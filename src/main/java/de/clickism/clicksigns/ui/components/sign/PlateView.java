package de.clickism.clicksigns.ui.components.sign;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clickui.UiComponent;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;

public class PlateView extends UiComponent<PlateView>
    implements ElementProvider {

    private final PlateElement element;
    private final ColorResolver colorResolver;

    public PlateView(PlateElement element, ColorResolver colorResolver) {
        this.element = element;
        this.colorResolver = colorResolver;
    }

    @Override
    protected void build() {
        var texture = element.frontSource().resolve(colorResolver);
        add(image(
            texture.location(),
            texture.width() * UI_SCALE,
            texture.height() * UI_SCALE
        ));
    }

    @Override
    public SignElement element() {
        return element;
    }
}
