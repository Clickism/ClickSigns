package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;

public class TextureButton extends UiComponent<TextureButton> {
    private final TextureSource source;
    private final Consumer<TextureSource> onTextureSelected;

    public TextureButton(TextureSource source, Consumer<TextureSource> onTextureSelected) {
        this.source = source;
        this.onTextureSelected = onTextureSelected;
    }

    @Override
    protected void build() {
        var texture = source.resize(16, 16).resolve(ColorResolver.empty());
        grow();
        add(image(texture.location(), 40, 40)
            .keepAspectRatio(true)
            .grow()
            .style(style()
                .whenHovered(style()
                    .borderColor(UiColor.RED)))
            .onClick(event -> {
                event.playSound();
                if (event.isLeftClick()) {
                    // Cycle to next texture in the same category
                    if (source instanceof TiledTextureSource tiled) {
                        var tileSet = tiled.resolveTileSet();
                        if (tileSet == null) return;
                        var nextTileSet = tileSet.nextInCategory();
                        var nextTexture = TiledTextureSource.unsized(nextTileSet.identifier());
                        onTextureSelected.accept(nextTexture);
                    }
                } else {
                    // Open texture menu
                    // TODO: Handle non-tile-set textures (e.g. custom textures)
                    var entries = SignRegistries.TILE_SETS.all().stream()
                        .map(tileSet -> new TextureList.Entry(
                            new TiledTextureSource(tileSet.identifier(), 16, 16)
                                .resolve(tileSet.colorResolver()),
                            tileSet.identifier(),
                            tileSet.resolveCategory()
                        ))
                        .toList();

                    new TextureSelectScreen(l("Select Texture"), entries)
                        // TODO: Confirm this also works well for non-tile-set textures
                        .textureScale(2) // Smaller scale for tilesets
                        .onTextureSelected(entry -> {
                            onTextureSelected.accept(TiledTextureSource.unsized(entry.identifier()));
                        }).open();
                }
            }));
    }
}
