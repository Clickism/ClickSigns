package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TextureButton extends UiComponent<TextureButton> implements FancyHeaders {
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
            .tooltip(descriptions(
                describeLeftClick(t("clicksigns.ui.textureButton.tooltip.leftClick")),
                describeRightClick(t("clicksigns.ui.textureButton.tooltip.rightClick"))
            ))
            .onClick(event -> {
                event.playSound();
                if (event.isLeftClick()) {
                    // TODO: Cycle to next texture in the same category
                    if (SignRegistries.TILE_SETS.has(source.base())) {
                        // Is tileset, cycle to next tileset in the same category
                        var tileSet = SignRegistries.TILE_SETS.get(source.base());
                        var nextTileSet = tileSet.nextInCategory();
                        var nextTexture = TextureSource.ofTiled(nextTileSet, 16, 16);
                        onTextureSelected.accept(nextTexture);
                    } else if (SignRegistries.STATIC_TEXTURES.has(source.base())) {
                        // Is static texture, cycle to next static texture in the same category
                        var staticTexture = SignRegistries.STATIC_TEXTURES.get(source.base());
                        var nextStaticTexture = staticTexture.nextInCategory();
                        var nextTexture = nextStaticTexture.textureSource();
                        onTextureSelected.accept(nextTexture);
                    }
                } else {
                    // Open texture menu
                    var tileSetEntries = SignRegistries.TILE_SETS.all().stream()
                        .map(tileSet -> new TextureList.Entry(
                            TextureSource.ofTiled(tileSet, 16, 16)
                                .resolve(ColorResolver.empty()),
                            tileSet.identifier(),
                            tileSet.resolveCategory()
                        ));

                    var staticEntries = SignRegistries.STATIC_TEXTURES.all().stream()
                        .map(staticTexture -> new TextureList.Entry(
                            staticTexture.textureSource().resolve(ColorResolver.empty()),
                            staticTexture.identifier(),
                            staticTexture.resolveCategory()
                        ));

                    var entries = Stream.concat(tileSetEntries, staticEntries)
                        .toList();

                    new TextureSelectScreen(t("clicksigns.ui.textureButton.textureMenu.header"), entries)
                        .textureScale(2) // Smaller scale for tilesets
                        .onTextureSelected(entry -> {
                            var tileSet = SignRegistries.TILE_SETS.get(entry.identifier());
                            if (tileSet != null) {
                                onTextureSelected.accept(TextureSource.ofTiled(tileSet, 16, 16));
                                return;
                            }
                            var staticTexture = SignRegistries.STATIC_TEXTURES.get(entry.identifier());
                            if (staticTexture != null) {
                                onTextureSelected.accept(staticTexture.textureSource());
                            }
                        }).open();
                }
            }));
    }
}
