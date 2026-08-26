package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clickui.Component;
import de.clickism.clickui.Element;
import de.clickism.clickui.reactivity.State;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * A UI component that displays a road sign with its texture and elements.
 */
public class SignView extends Component<SignView> {
    private final State<RoadSign> roadSign = state(RoadSign.DEFAULT);
    private final List<ElementProvider> elementProviders = new ArrayList<>();

    /**
     * Sets the road sign to be displayed in this SignView.
     *
     * @param sign the road sign to display
     * @return this SignView instance for method chaining
     */
    public SignView roadSign(@NotNull RoadSign sign) {
        this.roadSign.update(sign);
        return this;
    }

    /**
     * Returns the list of element providers associated with this SignView.
     *
     * @return the list of element providers
     */
    public List<ElementProvider> elementProviders() {
        return this.elementProviders;
    }

    /**
     * Adds the given ui element, and if it implements ElementProvider, adds it to the list of element providers.
     *
     * @param element the ui element to add
     */
    private void addAndPositionElement(Element<?> element) {
        this.add(element);
        if (element instanceof ElementProvider provider) {
            this.elementProviders.add(provider);
            var signElement = provider.element();
            // Position in UI space
            float x = signElement.alignedX() * DEFAULT_TEXTURE_RENDER_SCALE;
            // Y position is inverted
            float signHeight = this.roadSign.get().height();
            float y = (signHeight - signElement.alignedY() - signElement.signHeight()) * DEFAULT_TEXTURE_RENDER_SCALE;

            element.relative((int) x, (int) y);
        }
    }

    @Override
    protected void build() {
        // Clear previous elements
        this.elementProviders.clear();
        // Add main texture
        var roadSign = this.roadSign.get();
        var texture = roadSign.frontTexture();
        add(GuiUtils.imageOf(texture));
        // Add elements
        // Add plate elements
        for (var plate : roadSign.plateElements()) {
            var plateElement = new PlateView(plate, roadSign.colorResolver());
            addAndPositionElement(plateElement);
        }
        // Add symbol elements
        for (var symbol : roadSign.symbolElements()) {
            var symbolElement = new SymbolView(symbol, roadSign.colorResolver());
            addAndPositionElement(symbolElement);
        }
        // Add text elements last to render on top of symbols
        for (var textElement : roadSign.textElements()) {
            // TODO: Proper text field
            var textField = new SignTextField(textElement, roadSign.colorResolver());
            addAndPositionElement(textField);
        }
    }

}
