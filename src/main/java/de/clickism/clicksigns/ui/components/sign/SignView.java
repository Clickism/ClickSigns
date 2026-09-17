package de.clickism.clicksigns.ui.components.sign;

import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.editable.EditableRoadSign;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.layout.Point;
import de.clickism.clickui.layout.Rect;
import de.clickism.clickui.layout.Size;
import de.clickism.clickui.render.RenderContext;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A UI component that displays a road sign with its texture and elements.
 */
public class SignView extends UiComponent<SignView> {
    /**
     * The editable road sign that this SignView displays and allows editing of.
     */
    private final EditableRoadSign roadSign;

    private BiConsumer<UiElement<?>, Editable<SignElement>> elementConfig =
        (uiElement, signElement) -> {};
    private BiConsumer<UiElement<?>, EditableRoadSign> signConfig =
        (uiElement, roadSign) -> {};

    /**
     * The bounds of the main sign texture, used for rendering guidelines.
     */
    private @Nullable UiElement<?> mainSignElement = null;

    private boolean renderGuidelines = false;

    /**
     * Creates a new SignView for the given EditableRoadSign.
     *
     * @param roadSign the editable road sign to display and edit
     */
    public SignView(EditableRoadSign roadSign) {
        this.roadSign = roadSign;
        this.roadSign.onSignChanged(this::invalidateTree);
    }

    /**
     * Sets a configuration consumer for the elements of the sign.
     *
     * @param config a consumer that configures each SignElement
     * @return this SignView instance for method chaining
     */
    public SignView elementConfig(BiConsumer<UiElement<?>, Editable<SignElement>> config) {
        this.elementConfig = config;
        this.invalidateTree();
        return this;
    }

    /**
     * Sets a configuration consumer for the sign itself.
     * (The main sign texture)
     *
     * @param config a consumer that configures the sign
     * @return this SignView instance for method chaining
     */
    public SignView signConfig(BiConsumer<UiElement<?>, EditableRoadSign> config) {
        this.signConfig = config;
        this.invalidateTree();
        return this;
    }

    /**
     * Sets whether to render guidelines for the sign.
     *
     * @param renderGuidelines true to render guidelines, false otherwise
     * @return this SignView instance for method chaining
     */
    public SignView renderGuidelines(boolean renderGuidelines) {
        this.renderGuidelines = renderGuidelines;
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
        // Calculate maximum bounds of the sign and its elements
        var maxBounds = maxRelativeBounds();
        // Add main texture
        var texture = roadSign.build().frontTexture();
        var mainSignElement = UiUtil.imageOf(texture)
            .relative(-maxBounds.x(), -maxBounds.y());
        this.mainSignElement = mainSignElement;
        signConfig.accept(mainSignElement, roadSign);
        add(mainSignElement);

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

    @Override
    public void render(RenderContext context) {
        if (!renderGuidelines) return;
        var children = children();
        if (children.isEmpty()) return;
        // Render guidelines for the main sign
        var mainSignBounds = children().get(0).bounds();
        renderGuidelinesFor(context, mainSignBounds);
        // Render guidelines for plate elements
        for (var child : children()) {
            if (child instanceof PlateView) {
                renderGuidelinesFor(context, child.bounds());
            }
        }
    }

    private void renderGuidelinesFor(RenderContext context, Rect bounds) {
        var graphics = context.graphics();
        graphics.pose().pushPose();
        graphics.pose().translate(0, 0, 10); // Render on top of other elements
        // Draw center lines
        var color = UiColor.RED.color();
        var lineWidth = 1;
        var x = bounds.x();
        var y = bounds.y();
        var width = bounds.width();
        var height = bounds.height();
        var centerX = x + width / 2;
        var centerY = y + height / 2;
        graphics.fill(centerX, y, centerX + lineWidth, y + height, color);
        graphics.fill(x, centerY, x + width, centerY + lineWidth, color);

        graphics.pose().popPose();
    }

    /**
     * Adds a view for the given EditableSignElement to the SignView.
     *
     * @param element   the EditableSignElement to add
     * @param maxBounds the maximum bounds of the sign and its elements
     */
    private void addElementView(Editable<SignElement> element, Rect maxBounds) {
        // Memoize element based on its id
        var view = createViewFor(element);
        var pos = elementPosition(element.current(), maxBounds);
        // If placeholder text is being rendered, we need to posiiton based on that instead of the actual text
        if (element.current() instanceof TextElement text
            && text.text().isEmpty()
            && !((SignTextField) view).listening()
        ) {
            pos = elementPosition(text.withText(t("clicksigns.textPlaceholder").getString()), maxBounds);
        }
        // Reposition
        view.relative(pos.x(), pos.y());
        // Configure once per element, so that event listeners aren't added multiple times
        memo(view.hashCode() + "-config", () -> {
            elementConfig.accept(view, element);
            return null;
        });
        // Add to the view
        add(view);
    }

    /**
     * Calculates the position of a SignElement relative to the maximum bounds of the sign.
     *
     * @param element   the SignElement for which to calculate the position
     * @param maxBounds the maximum bounds of the sign and its elements
     * @return a Point representing the position of the SignElement relative to the maximum bounds
     */
    private Point elementPosition(SignElement element, Rect maxBounds) {
        float x = element.alignedX() * UI_SCALE;
        // Y position is inverted
        float signHeight = this.roadSign.build().height();
        float y = (signHeight - element.alignedY()
                   - element.height()) * UI_SCALE;
        return new Point((int) x - maxBounds.x(), (int) y - maxBounds.y());
    }

    /**
     * Creates a UI element view for the given SignElement based on its type.
     *
     * @param editableElement the sign element for which to create a view
     * @return a UiElement representing the view for the given SignElement
     */
    protected UiElement<?> createViewFor(Editable<SignElement> editableElement) {
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
            maxX = Mth.ceil(Math.max(maxX, element.alignedX() + element.width()));
            maxY = Mth.ceil(Math.max(maxY, element.alignedY() + element.height()));
        }

        int width = Mth.ceil((maxX - minX) * UI_SCALE);
        int height = Mth.ceil((maxY - minY) * UI_SCALE);

        minX = Mth.floor(minX * UI_SCALE);
        // Convert to UI coord
        minY = sign.height() * UI_SCALE
               - Mth.floor(maxY * UI_SCALE);

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

    /**
     * Converts a point in local sign coordinates to screen coordinates.
     *
     * @param local the point in local sign coordinates
     * @return the corresponding point in screen coordinates
     */
    public Point screenPositionOf(Point local) {
        if (mainSignElement == null) {
            throw new IllegalStateException("SignView has not been built yet.");
        }
        var bounds = mainSignElement.bounds();
        return new Point(
            bounds.x() + local.x() * UI_SCALE,
            bounds.y() + bounds.height() - local.y() * UI_SCALE
        );
    }
}
