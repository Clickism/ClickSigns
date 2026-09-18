package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.ui.UiConstants;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.layout.Align;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.function.Consumer;

public class TextureSelectScreen extends UiScreen<TextureSelectScreen> implements CommonComponents {
    private final Component title;
    private final Collection<TextureList.Entry> entries;
    private final UiColor backgroundColor;
    private Consumer<TextureList.Entry> onTextureSelected = entry -> {};
    private float textureScale = UiConstants.UI_SCALE;

    public TextureSelectScreen(Component title, Collection<TextureList.Entry> entries, UiColor backgroundColor) {
        this.title = title;
        this.entries = entries;
        this.backgroundColor = backgroundColor;
    }

    public TextureSelectScreen(Component title, Collection<TextureList.Entry> entries) {
        this(title, entries, UiColor.BLACK.alpha(0.5f));
    }

    public TextureSelectScreen onTextureSelected(Consumer<TextureList.Entry> onTextureSelected) {
        this.onTextureSelected = onTextureSelected;
        return this;
    }

    public TextureSelectScreen textureScale(float textureScale) {
        this.textureScale = textureScale;
        invalidateTree();
        return this;
    }

    @Override
    public void build() {
        this.alignCenter()
            .grow()
            .padding(8)
            .childGap(8)
            .children(
                islandHeader(title),
                darkBoxOutlined()
                    .grow()
                    .scrollable(false)
                    .maxHeight(400)
                    .crossAlign(Align.CENTER)
                    .maxWidth(300)
                    .style(style()
                        .backgroundColor(backgroundColor))
                    .children(
                        new TextureList(entries, textureScale)
                            .onTextureSelected(texture -> {
                                onTextureSelected.accept(texture);
                                close();
                            })
                            .grow()
                    )
            );
    }
}
