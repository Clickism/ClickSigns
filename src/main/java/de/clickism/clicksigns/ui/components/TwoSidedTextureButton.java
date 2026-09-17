package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clickui.UiComponent;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A UI component that displays two texture buttons side by side, one for the front texture and one for the back texture.
 */
public class TwoSidedTextureButton extends UiComponent<TwoSidedTextureButton> implements CommonComponents {
    private TextureSource frontSource;
    private TextureSource backSource;
    private final ColorResolver colorResolver;

    private Consumer<TextureSource> onFrontSelected = source -> {};
    private Consumer<TextureSource> onBackSelected = source -> {};

    public TwoSidedTextureButton(TextureSource frontSource, TextureSource backSource, ColorResolver colorResolver) {
        this.frontSource = frontSource;
        this.backSource = backSource;
        this.colorResolver = colorResolver;
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
                box()
                    .growWidth()
                    .childGap(4)
                    .children(
                        smallHeader(t("clicksigns.ui.textures.front")).padding(0),
                        new TextureButton(frontSource, colorResolver)
                            .onTextureSelected(onFrontSelected)),
                box()
                    .growWidth()
                    .childGap(4)
                    .children(
                        smallHeader(t("clicksigns.ui.textures.back")).padding(0),
                        new TextureButton(backSource, colorResolver)
                            .onTextureSelected(onBackSelected))
            )
        );
    }
}
