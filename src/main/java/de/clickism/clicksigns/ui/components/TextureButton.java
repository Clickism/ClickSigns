package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.screen.texture.TextureList;
import de.clickism.clicksigns.ui.screen.texture.TextureSelectScreen;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TextureButton extends UiComponent<TextureButton> implements CommonComponents {
    /**
     * The size of the texture to display in the button, in pixels.
     * This is used to resize the texture source.
     */
    public static final int TEXTURE_SIZE = 16;

    private final TextureSource source;
    private final ColorResolver colorResolver;
    private Consumer<TextureSource> onTextureSelected;

    public TextureButton(TextureSource source, ColorResolver colorResolver) {
        this.source = source;
        this.colorResolver = colorResolver;
    }

    public TextureButton onTextureSelected(Consumer<TextureSource> onTextureSelected) {
        this.onTextureSelected = onTextureSelected;
        return this;
    }

    @Override
    protected void build() {
        var texture = source.resize(TEXTURE_SIZE, TEXTURE_SIZE)
            .resolve(colorResolver);
        grow();
        add(image(texture.location(), TEXTURE_SIZE, TEXTURE_SIZE)
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
                    if (SignRegistries.TILE_SETS.has(source.base())) {
                        // Is tileset, cycle to next tileset in the same category
                        var tileSet = SignRegistries.TILE_SETS.get(source.base());
                        var nextTileSet = tileSet.nextInCategory();
                        var nextTexture = nextTileSet.textureSource(TEXTURE_SIZE, TEXTURE_SIZE);
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
                            tileSet.textureSource(TEXTURE_SIZE, TEXTURE_SIZE)
                                .resolve(colorResolver),
                            tileSet.identifier(),
                            tileSet.resolveCategory()
                        ));

                    var staticEntries = SignRegistries.STATIC_TEXTURES.all().stream()
                        .map(staticTexture -> new TextureList.Entry(
                            staticTexture.textureSource().resolve(colorResolver),
                            staticTexture.identifier(),
                            staticTexture.resolveCategory()
                        ));

                    var entries = Stream.concat(tileSetEntries, staticEntries)
                        .toList();

                    new TextureSelectScreen(t("clicksigns.ui.textureButton.textureMenu.header"), entries)
                        .textureScale(3.0f) // Smaller scale for tilesets
                        .onTextureSelected(entry -> {
                            var tileSet = SignRegistries.TILE_SETS.get(entry.identifier());
                            if (tileSet != null) {
                                onTextureSelected.accept(tileSet.textureSource(TEXTURE_SIZE, TEXTURE_SIZE));
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
