package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.UiScreen;

import java.util.function.Consumer;

public class TextureProcessorSelectScreen extends UiScreen<TextureProcessorSelectScreen> implements CommonComponents {
    private Consumer<TextureProcessor> onProcessorSelected = processor -> {};

    public TextureProcessorSelectScreen onProcessorSelected(Consumer<TextureProcessor> onProcessorSelected) {
        this.onProcessorSelected = onProcessorSelected;
        return this;
    }

    @Override
    protected void build() {
        grow();
        alignCenter();
        add(
            darkBoxOutlined()
                .children(
                    processorButton(new Tiler(4, 32, 32)),
                    processorButton(new ReplaceColor("", "green"))
                )
        );
    }

    private UiElement<?> processorButton(TextureProcessor processor) {
        return button(processor.getClass().getSimpleName())
            .growWidth()
            .onClick(event -> {
                event.playSound();
                onProcessorSelected.accept(processor);
                event.screen().close();
            });
    }
}
