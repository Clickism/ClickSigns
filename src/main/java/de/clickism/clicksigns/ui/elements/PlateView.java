package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clickui.Component;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

public class PlateView extends Component<PlateView>
    implements ElementProvider {

    private final PlateElement element;
    private final ColorResolver colorResolver;

    public PlateView(PlateElement element, ColorResolver colorResolver) {
        this.element = element;
        this.colorResolver = colorResolver;
    }

    @Override
    protected void build() {
        var texture = element.front().resolve(colorResolver);
        add(image(
            texture.location(),
            texture.width() * DEFAULT_TEXTURE_RENDER_SCALE,
            texture.height() * DEFAULT_TEXTURE_RENDER_SCALE
        ));
    }

    @Override
    public SignElement element() {
        return element;
    }
}
