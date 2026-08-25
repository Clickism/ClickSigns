package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clickui.Component;
import de.clickism.clickui.Element;
import de.clickism.clickui.reactivity.State;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector2i;

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

    // TODO: Refactor
    private Vector2f alignedLocalPos(SignElement element) {
        // Calculate in sign space
        float x = element.localX();
        float y = element.localY();
        float width = element.signWidth();
        float height = element.signHeight();
        // Adjust for center origin
        var halfWidth = width / 2f;
        var halfHeight = height / 2f;
        x -= halfWidth;
        y -= halfHeight;
        // Align
        var offset = element.alignment().offset();
        x += offset.x * halfWidth;
        y += offset.y * halfHeight;
        return new Vector2f(x, y);
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
            // Position
            var pos = alignedLocalPos(signElement);
            // Position in UI space
            float x = pos.x() * DEFAULT_TEXTURE_RENDER_SCALE;
            // Y position is inverted, and images render below origin in UI
            float signHeight = this.roadSign.get().height();
            float y = (signHeight - pos.y() - signElement.signHeight()) * DEFAULT_TEXTURE_RENDER_SCALE;

            element.relative((int) x, (int) y);
        }
    }

    @Override
    protected void build() {
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
            var textField = new SignTextField(textElement).width(20);
            addAndPositionElement(textField);
        }
    }

}
