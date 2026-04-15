package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A scrollable list of symbols, organized by category.
 */
public class SymbolList extends VerticalScrollContainer {

    private final Consumer<Texture> onSymbolSelected;

    /**
     * Creates a new symbol list.
     *
     * @param x                the x position of the list
     * @param y                the y position of the list
     * @param width            the width of the list
     * @param height           the height of the list
     * @param onSymbolSelected callback for when a symbol is selected
     */
    public SymbolList(int x, int y, int width, int height, Consumer<Texture> onSymbolSelected) {
        super(x, y, width, height);
        this.onSymbolSelected = onSymbolSelected;
        // Add categories
        for (int i = 0; i < 20; i++) {
            SymbolRegistry.allCategories().forEach(category -> {
                addChild(new StringWidget(0, 0, this.width - 20, 20, Component.literal(category), GuiUtils.font()));
                List<Texture> symbols = SymbolRegistry.allInCategory(category).stream()
                        .map(Texture::load)
                        .collect(Collectors.toList());
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
        public SymbolGrid(List<Texture> symbols, int gridWidth) {
            super(0, 0);
            this.gridWidth = gridWidth;
            addChildren(symbols.stream()
                    .map(texture -> new SymbolWidget(0, 0, texture))
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
            /**
             * Creates a new symbol widget.
             *
             * @param x       the x position of the widget
             * @param y       the y position of the widget
             * @param texture the texture to render for the symbol
             */
            public SymbolWidget(int x, int y, Texture texture) {
                super(x, y, texture, Color.WHITE.getRGB());
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                SymbolList.this.onSymbolSelected.accept(this.texture);
            }
        }
    }
}
