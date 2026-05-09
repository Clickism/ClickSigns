package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.gui.widget.texture.TextureWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Widget for rendering a road sign with its texture and elements.
 */
public class SignWidget extends NestedWidget {
    protected final Screen parent;
    protected @Nullable RoadSign lastRoadSign;
    protected final List<ElementProvider> elementProviders = new ArrayList<>();

    protected final TextWidgetFactory textFactory;
    protected final SymbolWidgetFactory symbolFactory;

    /**
     * Creates a new preview sign widget at the given position, displaying the given road sign.
     * Will use {@link TextWidget} and {@link SymbolWidget} for rendering text and symbol elements.
     *
     * @param x        the x position of the widget
     * @param y        the y position of the widget
     * @param roadSign the road sign to display, or null to create an empty widget
     * @param parent   the parent screen for symbol menus
     */
    public SignWidget(int x, int y, @Nullable RoadSign roadSign, @Nullable Screen parent) {
        this(x, y, roadSign, TextWidget::new, SymbolWidget::new, parent);
    }

    /**
     * Creates a new custom sign widget at the given position, displaying the given road sign.
     * Uses the given factories for creating element widgets.
     *
     * @param x             the x position of the widget
     * @param y             the y position of the widget
     * @param roadSign      the road sign to display, or null to create an empty widget
     * @param textFactory   the factory to use for creating text widgets
     * @param symbolFactory the factory to use for creating symbol widgets
     * @param parent        the parent screen for symbol menus
     */
    public SignWidget(
            int x, int y,
            @Nullable RoadSign roadSign,
            TextWidgetFactory textFactory,
            SymbolWidgetFactory symbolFactory,
            @Nullable Screen parent
    ) {
        super(x, y);
        this.parent = parent;
        this.textFactory = textFactory;
        this.symbolFactory = symbolFactory;
        if (roadSign == null) return;
        roadSign(roadSign);
        this.lastRoadSign = roadSign;
    }

    /**
     * Changes the displayed road sign of this widget.
     *
     * @param roadSign the new road sign to display
     */
    public void roadSign(RoadSign roadSign) {
        this.clearChildren();
        this.elementProviders.clear();
        // Add road sign texture
        var textureWidget = new TextureWidget(this.getX(), this.getY(), roadSign.frontTexture());
        this.addChild(textureWidget);
        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = symbolFactory.create(anchorX, anchorY, symbol, roadSign.colorResolver(), parent);
                this.addChild(symbolWidget);
                this.elementProviders.add(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = textFactory.create(anchorX, anchorY, textElement, roadSign.colorResolver(), roadSign.width());
                this.addChild(textBox);
                this.elementProviders.add(textBox);
            }
        }
        this.updateSize();
    }

    /**
     * Refreshes the displayed road sign and elements.
     */
    public void refresh() {
        if (lastRoadSign == null) return;
        roadSign(lastRoadSign);
    }

    /**
     * Gets the list of element providers for the currently displayed sign elements.
     *
     * @return the list of element providers
     */
    public List<ElementProvider> elementProviders() {
        return elementProviders;
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        this.refresh();
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        this.refresh();
    }

    /**
     * Factory interface for text widgets
     */
    public interface TextWidgetFactory {
        /**
         * Creates a new text widget
         */
        TextWidget create(int anchorX, int anchorY, TextElement element, ColorResolver colorResolver, int signWidth);
    }

    /**
     * Factory interface for symbol widgets
     */
    public interface SymbolWidgetFactory {
        /**
         * Creates a new symbol widget
         */
        SymbolWidget create(int anchorX, int anchorY, SymbolElement element, ColorResolver colorResolver, @Nullable Screen parent);
    }
}
