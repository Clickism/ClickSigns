package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.layout.Rect;
import de.clickism.clickui.layout.Size;
import de.clickism.clickui.reactivity.State;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * A UI component that displays a road sign with its texture and elements.
 */
public class SignView extends UiComponent<SignView> {
    private final State<RoadSign> roadSign = state(RoadSign.DEFAULT);
    private final List<ElementProvider> elementProviders = new ArrayList<>();

    // TODO: Guidelines

    private BiConsumer<UiElement<?>, SignElement> elementConfig = (uiElement, signElement) -> {};

    /**
     * Sets the road sign to be displayed in this SignView.
     *
     * @param sign the road sign to display
     * @return this SignView instance for method chaining
     */
    public SignView roadSign(@NotNull RoadSign sign) {
        // TODO: Make sure that this memo/rebuild cycle makes sense
        // Clear memo since the elements can be different now
        this.clearMemo();
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
     * Returns a list of SignElements provided by the element providers in this SignView.
     *
     * @return a list of SignElements
     */
    public List<SignElement> readElements() {
        return this.elementProviders.stream()
            .map(ElementProvider::element)
            .toList();
    }

    /**
     * Sets a configuration consumer for the elements of the sign.
     *
     * @param config a consumer that configures each SignElement
     * @return this SignView instance for method chaining
     */
    public SignView elementConfig(BiConsumer<UiElement<?>, SignElement> config) {
        this.elementConfig = config;
        this.invalidateTree();
        return this;
    }

    @Override
    public Size intrinsicSize() {
        var bounds = maxRelativeBounds();
        return new Size(
            bounds.width(),
            bounds.height()
        );
    }

    /**
     * Adds the given ui element, and if it implements ElementProvider, adds it to the list of element providers.
     *
     * @param element the ui element to add
     */
    private void addAndPositionElement(UiElement<?> element, Rect maxBounds) {
        this.add(element);
        if (element instanceof ElementProvider provider) {
            this.elementProviders.add(provider);
            var signElement = provider.element();
            // Position in UI space
            float x = signElement.alignedX() * DEFAULT_TEXTURE_RENDER_SCALE;
            // Y position is inverted
            float signHeight = this.roadSign.get().height();
            float y = (signHeight - signElement.alignedY() - signElement.signHeight()) * DEFAULT_TEXTURE_RENDER_SCALE;

            element.relative((int) x - maxBounds.x(), (int) y - maxBounds.y());
        }
    }

    @Override
    protected void build() {
        // Calculate maximum bounds of the sign and its elements
        var maxBounds = maxRelativeBounds();
        // Clear previous elements
        this.elementProviders.clear();
        // Add main texture
        var roadSign = this.roadSign.get();
        var texture = roadSign.frontTexture();

        add(GuiUtils.imageOf(texture)
            .relative(-maxBounds.x(), -maxBounds.y()));

        // Add elements
        roadSign.plateElements().forEach(element -> addUiElementFor(element, maxBounds));
        roadSign.symbolElements().forEach(element -> addUiElementFor(element, maxBounds));
        roadSign.textElements().forEach(element -> addUiElementFor(element, maxBounds));
    }

    /**
     * Creates, positions, and configures a UI element for the given SignElement.
     *
     * @param element   the SignElement for which to create a UI element
     * @param maxBounds the maximum bounds of the sign and its elements, used for positioning
     */
    private void addUiElementFor(SignElement element, Rect maxBounds) {
        var elementView = memo(() -> createViewFor(element));
        addAndPositionElement(elementView, maxBounds);
        elementConfig.accept(elementView, element);
    }

    /**
     * Creates a UI element view for the given SignElement based on its type.
     *
     * @param element the SignElement for which to create a view
     * @return a UiElement representing the view for the given SignElement
     */
    protected UiElement<?> createViewFor(SignElement element) {
        var colorResolver = roadSign.get().colorResolver();
        if (element instanceof PlateElement plate) {
            return new PlateView(plate, colorResolver);
        } else if (element instanceof SymbolElement symbol) {
            return new SymbolView(symbol, colorResolver);
        } else if (element instanceof TextElement text) {
            return new SignTextField(text, colorResolver);
        } else {
            throw new IllegalArgumentException("Unknown SignElement type: " + element.getClass().getName());
        }
    }

    /**
     * Calculates the maximum relative bounds of the sign and its elements.
     *
     * @return a Rect representing the maximum relative bounds of the sign and its elements
     */
    // TODO: Move into roadSign itself?
    private Rect maxRelativeBounds() {
        // Size based on bounds of the elements
        var sign = roadSign.get();
        // Relative bounds
        int minX = 0;
        int minY = 0;
        int maxX = sign.width();
        int maxY = sign.height();

        for (var provider : elementProviders) {
            var element = provider.element();
            minX = Mth.floor(Math.min(minX, element.alignedX()));
            minY = Mth.floor(Math.min(minY, element.alignedY()));
            maxX = Mth.ceil(Math.max(maxX, element.alignedX() + element.signWidth()));
            maxY = Mth.ceil(Math.max(maxY, element.alignedY() + element.signHeight()));
        }

        int width = Mth.ceil((maxX - minX) * DEFAULT_TEXTURE_RENDER_SCALE);
        int height = Mth.ceil((maxY - minY) * DEFAULT_TEXTURE_RENDER_SCALE);

        minX = Mth.floor(minX * DEFAULT_TEXTURE_RENDER_SCALE);
        // Convert to UI coord
        minY = sign.height() * DEFAULT_TEXTURE_RENDER_SCALE
               - Mth.floor(maxY * DEFAULT_TEXTURE_RENDER_SCALE);

        // Give in UI coordinates
        return new Rect(minX, minY, width, height);
    }
}
