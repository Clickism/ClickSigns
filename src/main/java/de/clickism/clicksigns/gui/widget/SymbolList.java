package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.util.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class SymbolList extends ContainerObjectSelectionList<SymbolList.Entry> {

    public SymbolList(Screen screen) {
        // TODO: Fix item height causing issues
        super(Minecraft.getInstance(), screen.width + 45, screen.height, 20, screen.height - 32, 60);
        // Add categories
        SymbolRegistry.allCategories().forEach(category -> {
            addEntry(new CategoryEntry(category));
            List<Texture> symbols = SymbolRegistry.allInCategory(category).stream()
                    .map(Texture::load)
                    .collect(Collectors.toList());
            addEntry(new SymbolsEntry(symbols));
        });
    }

    /**
     * Main entry class
     */
    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

    /**
     * Category entry
     */
    public static class CategoryEntry extends Entry {
        private final String name;
        private final int width;

        public CategoryEntry(String name) {
            this.name = name;
            this.width = GuiUtils.font().width(this.name);
        }

        @Override
        public void render(GuiGraphics guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            var minecraft = Minecraft.getInstance();
            var screen = minecraft.screen;
            if (screen == null) return;

            // TODO: Check the random values
            int x = minecraft.screen.width / 2 - width / 2;
            int y = j + m;
            guiGraphics.drawString(GuiUtils.font(), name, x, y - 9 - 1, Color.WHITE.getRGB(), false);
        }
    }

    /**
     * Symbols entry
     */
    public static class SymbolsEntry extends Entry {
        private static final int SYMBOL_SPACING = 2;
        private static final int MAX_WIDTH = 200;

        private final List<SymbolWidget> symbolWidgets;

        public SymbolsEntry(List<Texture> symbols) {
            this.symbolWidgets = symbols.stream()
                    .map(texture -> new SymbolWidget(0, 0, texture))
                    .toList();
        }

        private void positionWidgets(int x, int y) {
            // Position into rows, try to fit as many symbols as possible per row
            var screen = GuiUtils.currentScreen();
            if (screen == null) return;
            var gridWidth = Math.min(MAX_WIDTH, screen.width - 40);

            // Position the widgets
            int startX = x - gridWidth / 2;
            int currentX = startX;
            int currentY = y;
            int maxHeightInRow = 0;
            for (var widget : symbolWidgets) {
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
        public void render(GuiGraphics guiGraphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            var minecraft = Minecraft.getInstance();
            var screen = minecraft.screen;
            if (screen == null) return;

            int widgetX = minecraft.screen.width / 2;
            int widgetY = y + entryHeight;
            positionWidgets(widgetX, widgetY);
            symbolWidgets.forEach(widget -> {
                widget.render(guiGraphics, mouseX, mouseY, tickDelta);
            });
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.copyOf(symbolWidgets);
        }
    }

    public static class SymbolWidget extends ClickableTextureWidget {
        public SymbolWidget(int x, int y, Texture texture) {
            super(x, y, texture, Color.WHITE.getRGB());
        }

        @Override
        public void onClick(double d, double e) {
            GuiUtils.closeScreen(); // TODO: Implement
        }
    }
}
