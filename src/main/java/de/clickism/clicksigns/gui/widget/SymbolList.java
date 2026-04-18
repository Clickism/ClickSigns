package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.ColorResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * A scrollable list of symbols, organized by category.
 */
public class SymbolList extends VerticalScrollContainer {

    private final ColorResolver colorResolver;
    private final Consumer<Symbol> onSymbolSelected;

    /**
     * Creates a new symbol list.
     *
     * @param x                the x position of the list
     * @param y                the y position of the list
     * @param width            the width of the list
     * @param height           the height of the list
     * @param colorResolver    the color resolver to use for resolving symbol textures
     * @param onSymbolSelected callback for when a symbol is selected
     */
    public SymbolList(int x, int y, int width, int height, ColorResolver colorResolver, Consumer<Symbol> onSymbolSelected) {
        super(x, y, width, height);
        this.colorResolver = colorResolver;
        this.onSymbolSelected = onSymbolSelected;
        // Add categories
        for (int i = 0; i < 20; i++) {
            SignRegistries.SYMBOLS.allCategories().forEach(category -> {
                addChild(new StringWidget(0, 0, this.width - 20, 20, Component.literal(category.name()), GuiUtils.font()));
                List<Symbol> symbols = category.resolveEntries();
                addChild(new SymbolGrid(symbols, width));
            });
        }
    }

    /**
     * Symbols entry
     */
    public class SymbolGrid extends NestedWidget {
        private static final int SYMBOL_SPACING = 2;

        private final int gridWidth;

        /**
         * Creates a new symbol grid.
         *
         * @param symbols   the symbols to display in the grid
         * @param gridWidth the maximum width of the grid, used to determine when to wrap to the next row
         */
        public SymbolGrid(List<Symbol> symbols, int gridWidth) {
            super(0, 0);
            this.gridWidth = gridWidth;
            addChildren(symbols.stream()
                    .map(symbol -> new SymbolWidget(0, 0, symbol))
                    // Sort by identifier for consistent order
                    .sorted(Comparator.comparing(widget -> widget.symbol.identifier().toString()))
                    .toList());
            positionWidgets();
            updateSize(); // Update size after positioning
        }

        private void positionWidgets() {
            // Position into rows, try to fit as many symbols as possible per row
            var screen = GuiUtils.currentScreen();
            if (screen == null) return;

            // Position the widgets
            int startX = 0;
            int currentX = startX;
            int currentY = 0;
            int maxHeightInRow = 0;
            for (var widget : children()) {
                if (currentX + widget.getWidth() > startX + gridWidth) {
                    // Move to next row
                    currentX = startX;
                    currentY += maxHeightInRow + SYMBOL_SPACING;
                }
                // Position the widget
                widget.setPosition(currentX, currentY);
                currentX += widget.getWidth() + SYMBOL_SPACING;
                maxHeightInRow = Math.max(maxHeightInRow, widget.getHeight());
            }
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
            var minecraft = Minecraft.getInstance();
            var screen = minecraft.screen;
            if (screen == null) return;
            super.renderWidget(graphics, mouseX, mouseY, delta);
        }

        /**
         * Widget for a single symbol.
         */
        public class SymbolWidget extends ClickableTextureWidget {
            private final Symbol symbol;

            /**
             * Creates a new symbol widget.
             *
             * @param x      the x position of the widget
             * @param y      the y position of the widget
             * @param symbol the texture to render for the symbol
             */
            public SymbolWidget(int x, int y, Symbol symbol) {
                super(x, y, symbol.texture().resolve(colorResolver), Color.WHITE.getRGB());
                this.symbol = symbol;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                SymbolList.this.onSymbolSelected.accept(this.symbol);
            }
        }
    }
}
