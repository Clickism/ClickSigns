package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.util.VerticalScrollContainer;
import de.clickism.clicksigns.gui.widget.texture.ClickableTextureWidget;
import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.sign.Category;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A scrollable list of textures, organized by category.
 */
public class TextureList<T extends Categorized<T>> extends VerticalScrollContainer {

    private final Consumer<ResourceLocation> onTextureSelected;

    /**
     * Creates a new texture list.
     *
     * @param x                 the x position of the list
     * @param y                 the y position of the list
     * @param width             the width of the list
     * @param height            the height of the list
     * @param onTextureSelected callback for when a texture is selected
     */
    public TextureList(int x, int y, int width, int height,
                       Map<Category<T>, Collection<IdentifiableTexture>> categoryToTextures,
                       Consumer<ResourceLocation> onTextureSelected
    ) {
        super(x, y, width, height);
        this.onTextureSelected = onTextureSelected;
        // Add categories
        // TODO: Add uncategorized textures at the end
        categoryToTextures.forEach((category, textures) -> {
            addChild(new CategoryHeaderWidget(this.width, category.name()));
            addChild(new TextureGrid(textures, width));
        });
    }

    /**
     * Record used for storing a texture along with its identifier, used for rendering the textures in the list.
     *
     * @param identifier the identifier of the texture, i.E. tile set name
     * @param texture    texture to render
     */
    public record IdentifiableTexture(ResourceLocation identifier, Texture texture) {
    }

    /**
     * Texture grid for a single category
     */
    public class TextureGrid extends NestedWidget {
        private static final int SYMBOL_SPACING = 2;

        private final int gridWidth;

        /**
         * Creates a new texture grid.
         *
         * @param textures  the textures to display in the grid
         * @param gridWidth the maximum width of the grid, used to determine when to wrap to the next row
         */
        public TextureGrid(Collection<IdentifiableTexture> textures, int gridWidth) {
            super(0, 0);
            this.gridWidth = gridWidth;
            addChildrenAndUpdate(textures.stream()
                    .map(texture -> new SingleTextureWidget(0, 0, texture))
                    // Sort by identifier for consistent order
                    .sorted(Comparator.comparing(widget -> widget.identifier.toString()))
                    .toList());
            positionWidgets();
            updateSizeAndPosition(); // Update size after positioning
        }

        private void positionWidgets() {
            // Position into rows, try to fit as many textures as possible per row
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
         * Widget for a single texture.
         */
        private class SingleTextureWidget extends ClickableTextureWidget {
            private final ResourceLocation identifier;

            /**
             * Creates a new texture widget.
             *
             * @param x       the x position of the widget
             * @param y       the y position of the widget
             * @param texture the texture to render for the texture
             */
            public SingleTextureWidget(int x, int y, IdentifiableTexture texture) {
                super(x, y, texture.texture, GuiUtils.OUTLINE_COLOR);
                this.identifier = texture.identifier;
            }

            @Override
            public void onClick(double mouseX, double mouseY) {
                TextureList.this.onTextureSelected.accept(this.identifier);
            }
        }
    }
}
