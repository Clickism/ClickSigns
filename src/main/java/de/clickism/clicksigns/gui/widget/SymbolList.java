package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.awt.Color;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolList extends VerticalScrollContainer {

    public SymbolList(int x, int y, int width, int height) {
        super(x, y, width, height);
        // Add categories
        for (int i = 0; i < 20; i++) {
            SymbolRegistry.allCategories().forEach(category -> {
                addChild(new StringWidget(0, 0, this.width - 20, 20, Component.literal(category), GuiUtils.font()));
                List<Texture> symbols = SymbolRegistry.allInCategory(category).stream()
                        .map(Texture::load)
                        .collect(Collectors.toList());
                addChild(new SymbolGrid(symbols));
            });
        }
    }

    /**
     * Symbols entry
     */
    public static class SymbolGrid extends NestedWidget {
        private static final int SYMBOL_SPACING = 2;
        private static final int MAX_WIDTH = 200;

        public SymbolGrid(List<Texture> symbols) {
            super(0, 0);
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
            var gridWidth = Math.min(MAX_WIDTH, screen.width - 40);

            // Position the widgets
            int startX = gridWidth / 2;
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

        public static class SymbolWidget extends ClickableTextureWidget {
            public SymbolWidget(int x, int y, Texture texture) {
                super(x, y, texture, Color.WHITE.getRGB());
            }

            @Override
            public void onClick(double d, double e) {
                GuiUtils.popScreen(); // TODO: Implement
            }
        }
    }
}
