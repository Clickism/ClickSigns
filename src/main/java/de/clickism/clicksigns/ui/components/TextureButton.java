package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.TextureCategory;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.screen.texture.TextureEditScreen;
import de.clickism.clicksigns.ui.screen.texture.TextureSelectScreen;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.style.Border;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TextureButton extends UiComponent<TextureButton> implements CommonComponents {
    /**
     * The size of the texture to display in the button, in pixels.
     * This is used to resize the texture source.
     */
    public static final int TEXTURE_SIZE = 16;

    private final TextureSource source;
    private final @Nullable TextureSource background;
    private final ColorResolver colorResolver;
    private final Collection<TextureCategory> categories;
    private Consumer<TextureSource> onTextureSelected = source -> {};

    public TextureButton(
        TextureSource source,
        @Nullable TextureSource background,
        ColorResolver colorResolver,
        Collection<TextureCategory> categories
    ) {
        this.source = source;
        this.background = background;
        this.colorResolver = colorResolver;
        this.categories = categories;
    }

    public TextureButton onTextureSelected(Consumer<TextureSource> onTextureSelected) {
        this.onTextureSelected = onTextureSelected;
        return this;
    }

    @Override
    protected void build() {
        grow();
        childGap(4);
        var imageBox = box()
            .grow();
        add(imageBox);
        var texture = source.resize(TEXTURE_SIZE, TEXTURE_SIZE)
            .resolve(colorResolver);
        imageBox.add(
            UiUtil.imageOf(texture)
                .keepAspectRatio(true)
                .grow()
        );
        imageBox
            .padding(background != null
                ? 4
                : 2)
            .style(style()
                .borderPosition(Border.Position.INSIDE)
                .backgroundColor(background != null
                    ? UiUtil.primaryColorOf(background.resolveImage(colorResolver))
                    : UiColor.BLACK.alpha(0.2f))
                .borderColor(background != null
                    ? UiColor.GRAY
                    : UiColor.LIGHT_GRAY.alpha(0.2f))
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
                    } else if (SignRegistries.SYMBOLS.has(source.base())) {
                        // Is symbol, cycle to next symbol in the same category
                        var symbol = SignRegistries.SYMBOLS.get(source.base());
                        var nextSymbol = symbol.nextInCategory();
                        var nextTexture = nextSymbol.textureSource();
                        onTextureSelected.accept(nextTexture);
                    }
                } else {
                    TextureSelectScreen.forTextureCategories(
                        background,
                        colorResolver,
                        categories,
                        onTextureSelected
                    ).open();
                }
            });
        add(button(t("✎", "clicksigns.ui.textures.edit"))
            .buttonColor(UiColor.TEAL)
            .growWidth()
            .height(14)
            .style(style()
                .fontScale(0.8f))
            .onClick(event -> {
                new TextureEditScreen(source, background, colorResolver, categories)
                    .onTextureEdited(onTextureSelected)
                    .open();
            }));
    }
}
