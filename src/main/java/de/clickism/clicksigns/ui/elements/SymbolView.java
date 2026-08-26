package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clickui.Component;

public class SymbolView extends Component<SymbolView>
    implements ElementProvider {

    private final SymbolElement element;
    private final ColorResolver colorResolver;

    public SymbolView(SymbolElement element, ColorResolver colorResolver) {
        this.element = element;
        this.colorResolver = colorResolver;
    }

    @Override
    protected void build() {
        var texture = element.symbol().texture().resolve(colorResolver);
        add(GuiUtils.imageOf(texture));
    }

    @Override
    public SignElement element() {
        return element;
    }
}
