package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.editor.EditableSignElement;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.layout.Point;
import de.clickism.clickui.layout.Rect;
import de.clickism.clickui.layout.Size;
import net.minecraft.util.Mth;

import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * A UI component that displays a road sign with its texture and elements.
 */
// TODO: Rename to Sign Editor
// TODO: Add way to disable text input
public class SignView extends UiComponent<SignView> {
    private final EditableRoadSign roadSign;
    // TODO: Guidelines

    private BiConsumer<UiElement<?>, EditableSignElement> elementConfig =
        (uiElement, signElement) -> {};
    private final Map<UUID, UiElement<?>> elementViews = new java.util.HashMap<>();

    public SignView(EditableRoadSign roadSign) {
        this.roadSign = roadSign;
        this.roadSign.addChangeListener(change -> {
            this.invalidateTree();
        });
    }

    /**
     * Sets a configuration consumer for the elements of the sign.
     *
     * @param config a consumer that configures each SignElement
     * @return this SignView instance for method chaining
     */
    public SignView elementConfig(BiConsumer<UiElement<?>, EditableSignElement> config) {
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

    @Override
    protected void build() {
        elementViews.clear();
        // Calculate maximum bounds of the sign and its elements
        var maxBounds = maxRelativeBounds();
        // Add main texture
        var texture = roadSign.build().frontTexture();

        add(GuiUtils.imageOf(texture)
            .relative(-maxBounds.x(), -maxBounds.y()));

        // Add plate elements
        roadSign.elements().stream()
            .filter(element -> element.current() instanceof PlateElement)
            .forEach(element -> addElementView(element, maxBounds));

        // Add symbol elements
        roadSign.elements().stream()
            .filter(element -> element.current() instanceof SymbolElement)
            .forEach(element -> addElementView(element, maxBounds));

        // Add text elements
        roadSign.elements().stream()
            .filter(element -> element.current() instanceof TextElement)
            .forEach(element -> addElementView(element, maxBounds));
    }

    /**
     * Adds a view for the given EditableSignElement to the SignView.
     *
     * @param element   the EditableSignElement to add
     * @param maxBounds the maximum bounds of the sign and its elements
     */
    private void addElementView(EditableSignElement element, Rect maxBounds) {
        // Memoize element based on its id
        var view = createViewFor(element);
        var pos = elementPosition(element.current(), maxBounds);
        // Reposition
        view.relative(pos.x(), pos.y());
        // Configure
        elementConfig.accept(view, element);
        // Add to the view
        add(view);
        elementViews.put(element.id(), view);
    }

    /**
     * Calculates the position of a SignElement relative to the maximum bounds of the sign.
     *
     * @param element   the SignElement for which to calculate the position
     * @param maxBounds the maximum bounds of the sign and its elements
     * @return a Point representing the position of the SignElement relative to the maximum bounds
     */
    private Point elementPosition(SignElement element, Rect maxBounds) {
        float x = element.alignedX() * DEFAULT_TEXTURE_RENDER_SCALE;
        // Y position is inverted
        float signHeight = this.roadSign.build().height();
        float y = (signHeight - element.alignedY()
                   - element.signHeight()) * DEFAULT_TEXTURE_RENDER_SCALE;
        return new Point((int) x - maxBounds.x(), (int) y - maxBounds.y());
    }

    /**
     * Creates a UI element view for the given SignElement based on its type.
     *
     * @param editableElement the sign element for which to create a view
     * @return a UiElement representing the view for the given SignElement
     */
    protected UiElement<?> createViewFor(EditableSignElement editableElement) {
        var colorResolver = roadSign.build().colorResolver();
        var element = editableElement.current();
        if (element instanceof PlateElement plate) {
            return new PlateView(plate, colorResolver);
        } else if (element instanceof SymbolElement symbol) {
            return new SymbolView(symbol, colorResolver);
        } else if (element instanceof TextElement text) {
            return memo(editableElement.id(), () -> new SignTextField(text, colorResolver))
                .textElement(text)
                .colorResolver(colorResolver)
                .onValueChanged(value -> {
                    roadSign.updateElement(
                        editableElement.id(),
                        old -> ((TextElement) old).withText(value)
                    );
                });
        } else {
            throw new IllegalArgumentException(
                "Unknown SignElement type: " + element.getClass().getName()
            );
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
        var sign = roadSign.build();
        // Relative bounds
        int minX = 0;
        int minY = 0;
        int maxX = sign.width();
        int maxY = sign.height();

        for (var element : sign.elements()) {
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

    /**
     * Resets the text field cache for all text elements in the sign view.
     * This is useful when the underlying element's text has changed.
     */
    public void resetTextFieldCache() {
        clearMemo();
        clearMemoKeys();
    }
}
