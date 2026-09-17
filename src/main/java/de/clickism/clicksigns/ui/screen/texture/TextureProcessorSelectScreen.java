package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.style.Border;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;

public class TextureProcessorSelectScreen extends UiScreen<TextureProcessorSelectScreen> implements CommonComponents {
    private Consumer<TextureProcessor> onProcessorSelected = processor -> {};

    public TextureProcessorSelectScreen onProcessorSelected(Consumer<TextureProcessor> onProcessorSelected) {
        this.onProcessorSelected = onProcessorSelected;
        return this;
    }

    @Override
    protected void build() {
        this.alignCenter()
            .grow()
            .padding(8)
            .childGap(8)
            .children(
                // TODO: convert into function
                h4(l("Select Texture Processor"))
                    .padding(6, 12)
                    .style(style()
                        .borderColor(UiColor.LIGHT_GRAY.alpha(0.5f))
                        .backgroundColor(UiColor.BLACK.alpha(0.5f))),

                darkBoxOutlined()
                    .maxWidth(200)
                    .padding(8)
                    .children(
                        processorButton(new Tiler(2, 16, 16)),
                        processorButton(new ReplaceColor("", "green"))
                    )
            );
    }

    private UiElement<?> processorButton(TextureProcessor processor) {
        return box()
            .growWidth()
            .height(24)
            .alignCenter()
            .style(style()
                .borderPosition(Border.Position.INSIDE)
                .whenHovered(style()
                    .backgroundColor(UiColor.WHITE_A20)
                    .borderColor(UiColor.WHITE)))
            .onClick(event -> {
                event.playSound();
                onProcessorSelected.accept(processor);
                event.screen().close();
            })
            .children(
                text(processor.getClass().getSimpleName())
            );
    }
}
