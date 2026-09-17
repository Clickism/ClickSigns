package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.screen.texture.TextureEditScreen;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A UI component that displays two texture buttons side by side, one for the front texture and one for the back texture.
 */
public class TwoSidedTextureButton extends UiComponent<TwoSidedTextureButton> implements CommonComponents {
    private final ColorResolver colorResolver;
    private final TextureSource frontSource;
    private TextureSource backSource;
    private Consumer<TextureSource> onFrontSelected = source -> {};
    private Consumer<TextureSource> onBackSelected = source -> {};
    private final Size desiredSize;

    public TwoSidedTextureButton(
        TextureSource frontSource,
        TextureSource backSource,
        ColorResolver colorResolver,
        Size desiredSize
    ) {
        this.frontSource = frontSource;
        this.backSource = backSource;
        this.colorResolver = colorResolver;
        this.desiredSize = desiredSize;
    }

    public TwoSidedTextureButton onFrontSelected(Consumer<TextureSource> onFrontSelected) {
        this.onFrontSelected = onFrontSelected;
        this.invalidateTree();
        return this;
    }

    public TwoSidedTextureButton onBackSelected(Consumer<TextureSource> onBackSelected) {
        this.onBackSelected = onBackSelected;
        this.invalidateTree();
        return this;
    }

    public TwoSidedTextureButton maskBack() {
        this.backSource = RoadSign.maskedBackOf(
            frontSource.resize(TextureButton.TEXTURE_SIZE, TextureButton.TEXTURE_SIZE),
            backSource
        );
        return this;
    }

    @Override
    protected void build() {
        this
            .growWidth()
            .children(box()
                .horizontal()
                .growWidth()
                .childGap(4)
                .children(
                    withHeader(
                        t("clicksigns.ui.textures.front"),
                        box()
                            .childGap(4)
                            .growWidth()
                            .children(
                                new TextureButton(frontSource, colorResolver)
                                    .onTextureSelected(textureSource ->
                                        onFrontSelected.accept(textureSource.resize(desiredSize))),
                                button(t("✎", "clicksigns.ui.textures.edit"))
                                    .buttonColor(UiColor.TEAL)
                                    .growWidth()
                                    .height(14)
                                    .onClick(event -> {
                                        new TextureEditScreen(frontSource, colorResolver)
                                            .onTextureEdited(onFrontSelected)
                                            .open();
                                    })
                            )
                    ),
                    withHeader(
                        t("clicksigns.ui.textures.back"),
                        box()
                            .childGap(4)
                            .growWidth()
                            .children(
                                new TextureButton(backSource, colorResolver)
                                    .onTextureSelected(textureSource ->
                                        onBackSelected.accept(textureSource.resize(desiredSize))),
                                button(t("✎", "clicksigns.ui.textures.edit"))
                                    .buttonColor(UiColor.TEAL)
                                    .growWidth()
                                    .height(14)
                                    .onClick(event -> {
                                        new TextureEditScreen(backSource, colorResolver)
                                            .onTextureEdited(onBackSelected)
                                            .open();
                                    })
                            )
                    )
                )
            );
    }
}
