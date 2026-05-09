package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.TextureMenuScreen;
import de.clickism.clicksigns.gui.widget.TextureList;
import de.clickism.clicksigns.gui.widget.texture.ClickableTextureWidget;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class EditableTextureWidget extends ClickableTextureWidget {
    private final Screen parent;
    private final Consumer<TextureSource> onTextureSelected;

    /**
     * Creates a new editable texture widget.
     *
     * @param parent            the parent screen to return to when closing the texture menu
     * @param x                 the x position of the widget
     * @param y                 the y position of the widget
     * @param texture           the initial texture to display
     * @param onTextureSelected callback for when a texture is selected.
     *                          There is no guarantee on the size of the texture source, so callback should handle resizing.
     */
    public EditableTextureWidget(Screen parent, int x, int y, Texture texture, Consumer<TextureSource> onTextureSelected) {
        super(x, y, texture, GuiUtils.OUTLINE_COLOR, 3);
        this.parent = parent;
        this.onTextureSelected = onTextureSelected;
        // TODO: Translate
        this.setTooltip(Tooltip.create(Component.literal("§f§lClick §rto cycle texture\n§f§lRight click §rto open texture menu")));
    }

    @Override
    public void onClick(double d, double e) {
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
    }
}
