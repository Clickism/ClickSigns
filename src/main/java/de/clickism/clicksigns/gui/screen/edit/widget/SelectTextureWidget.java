package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.TextureMenuScreen;
import de.clickism.clicksigns.gui.widget.TextureList;
import de.clickism.clicksigns.gui.widget.texture.ClickableTextureWidget;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class SelectTextureWidget extends ClickableTextureWidget {
    private final Screen parent;
    private final TextureSource textureSource;
    private final Consumer<TextureSource> onTextureSelected;

    /**
     * Creates a new editable texture widget.
     *
     * @param parent            the parent screen to return to when closing the texture menu
     * @param x                 the x position of the widget
     * @param y                 the y position of the widget
     * @param textureSource     the initial texture to display
     * @param onTextureSelected callback for when a texture is selected.
     *                          There is no guarantee on the size of the texture source, so callback should handle resizing.
     */
    public SelectTextureWidget(
            Screen parent, int x, int y,
            TextureSource textureSource,
            ColorResolver colorResolver,
            Consumer<TextureSource> onTextureSelected
    ) {
        super(x, y, textureSource.resize(16, 16).resolve(colorResolver), GuiUtils.OUTLINE_COLOR, 3);
        this.parent = parent;
        this.textureSource = textureSource;
        this.onTextureSelected = onTextureSelected;
        // TODO: Translate
        this.setTooltip(Tooltip.create(Component.literal("§f§lClick §rto cycle texture\n§f§lRight click §rto open texture menu")));
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return GuiUtils.isLeftClick(i) || GuiUtils.isRightClick(i);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        if (GuiUtils.isLeftClick(button)) {
            // Cycle to next tile set in the same category
            if (textureSource instanceof TiledTextureSource tiled) {
                var tileSet = tiled.resolveTileSet();
                if (tileSet == null) return false;
                var nextTileSet = tileSet.nextInCategory();
                var nextTexture = TiledTextureSource.unsized(nextTileSet.identifier());
                onTextureSelected.accept(nextTexture);
            }
            return true;
        }
        if (GuiUtils.isRightClick(button)) {
            // Open texture menu
            var categoryToTextures = SignRegistries.TILE_SETS.categoryToEntriesAndThen(tileSet -> new TextureList.IdentifiableTexture(
                    tileSet.identifier(),
                    new TiledTextureSource(tileSet.identifier(), 16, 16).resolve(tileSet.colorResolver())
            ));
            var screen = new TextureMenuScreen<>(parent, categoryToTextures, texture -> {
                var source = TiledTextureSource.unsized(texture);
                onTextureSelected.accept(source);
                GuiUtils.closeScreen();
            });
            GuiUtils.openScreen(screen);
            return true;
        }
        return false;
    }


}
