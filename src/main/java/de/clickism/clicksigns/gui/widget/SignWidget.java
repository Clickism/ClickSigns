package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.widget.texture.TextureWidget;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * Widget for rendering a road sign with its texture and elements.
 */
public class SignWidget extends NestedWidget {
    protected final Screen parent;
    protected @Nullable RoadSign lastRoadSign;
    protected final List<ElementProvider> elementProviders = new ArrayList<>();

    protected final ElementWidgetFactory<TextElement> textFactory;
    protected final ElementWidgetFactory<SymbolElement> symbolFactory;
    protected final ElementWidgetFactory<PlateElement> plateFactory;

    protected @Nullable TextureWidget textureWidget;

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
            ElementWidgetFactory<TextElement> textFactory,
            ElementWidgetFactory<SymbolElement> symbolFactory,
            ElementWidgetFactory<PlateElement> plateFactory,
            @Nullable Screen parent
    ) {
        super(x, y);
        this.parent = parent;
        this.textFactory = textFactory;
        this.symbolFactory = symbolFactory;
        this.plateFactory = plateFactory;
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
        textureWidget = new TextureWidget(this.getX(), this.getY(), roadSign.frontTexture());
        this.addChild(textureWidget);
        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        // Add plate elements
        for (var element : roadSign.elements()) {
            if (element instanceof PlateElement plate) {
                var plateWidget = plateFactory.create(anchorX, anchorY, plate, roadSign, parent);
                this.addChild(plateWidget);
                if (plateWidget instanceof ElementProvider provider) {
                    this.elementProviders.add(provider);
                }
            }
        }
        // Add symbol elements
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = symbolFactory.create(anchorX, anchorY, symbol, roadSign, parent);
                this.addChild(symbolWidget);
                if (symbolWidget instanceof ElementProvider provider) {
                    this.elementProviders.add(provider);
                }
            }
        }
        // Add text elements last to render on top of symbols
        for (var element : roadSign.elements()) {
            if (element instanceof TextElement textElement) {
                var textBox = textFactory.create(anchorX, anchorY, textElement, roadSign, parent);
                this.addChild(textBox);
                if (textBox instanceof ElementProvider provider) {
                    this.elementProviders.add(provider);
                }
            }
        }
        this.updateSizeAndPosition();
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
     * Factory interface for element widgets
     * <p>
     * The returned widget must implement {@link ElementProvider} to provide the corresponding element.
     */
    public interface ElementWidgetFactory<T extends SignElement> {
        /**
         * Creates a new widget for the given element.
         */
        AbstractWidget create(
                int anchorX,
                int anchorY,
                T element,
                RoadSign roadSign,
                @Nullable Screen parent
        );
    }

    /**
     * Widget for rendering guidelines on the sign widget.
     */
    public class GuidelinesWidget implements Renderable {
        private static final int LINE_WIDTH = 1;
        private static final int LINE_COLOR = new Color(255, 50, 50, 200).getRGB();

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, float f) {
            if (lastRoadSign == null || textureWidget == null) return;
            int x = textureWidget.getX();
            int y = textureWidget.getY();
            var signWidth = lastRoadSign.width() * DEFAULT_TEXTURE_RENDER_SCALE;
            var signHeight = lastRoadSign.height() * DEFAULT_TEXTURE_RENDER_SCALE;
            var centerX = x + signWidth / 2;
            var centerY = y + signHeight / 2;
            guiGraphics.fill(centerX, y, centerX + LINE_WIDTH, y + signHeight, LINE_COLOR);
            guiGraphics.fill(x, centerY, x + signWidth, centerY + LINE_WIDTH, LINE_COLOR);
        }
    }
}
