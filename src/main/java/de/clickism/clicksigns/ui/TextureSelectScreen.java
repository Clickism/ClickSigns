package de.clickism.clicksigns.ui;

import de.clickism.clickui.Element;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.layout.Align;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.function.Consumer;

public class TextureSelectScreen extends UiScreen {

    private final Component title;
    private final Collection<TextureList.Entry> entries;

    private Consumer<TextureList.Entry> onTextureSelected = entry -> {};

    public TextureSelectScreen(Component title, Collection<TextureList.Entry> entries) {
        this.title = title;
        this.entries = entries;
    }

    public TextureSelectScreen onTextureSelected(Consumer<TextureList.Entry> onTextureSelected) {
        this.onTextureSelected = onTextureSelected;
        return this;
    }

    @Override
    public Element<?> build() {
        return box()
            .alignCenter()
            .grow()
            .padding(8)
            .childGap(8)
            .children(
                h4(title)
                    .padding(6, 12)
                    .style(s -> s
                        .border(UiColor.LIGHT_GRAY.alpha(0.5f))
                        .background(UiColor.BLACK.alpha(0.5f))),
                box()
                    .grow()
                    .scrollable(false)
                    .maxHeight(400)
                    .crossAlign(Align.CENTER)
                    .maxWidth(300)
                    .style(s -> s
                        .border(UiColor.LIGHT_GRAY.alpha(0.5f))
                        .background(UiColor.BLACK.alpha(0.5f))
                    )
                    .children(
                        new TextureList(entries)
                            .onTextureSelected(texture -> {
                                onTextureSelected.accept(texture);
                                close();
                            })
                            .grow()
                    )
            );
    }
}
