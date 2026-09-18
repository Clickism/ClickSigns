package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.style.Border;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * Screen for selecting a {@link TextureProcessor}.
 */
public class TextureProcessorSelectScreen extends UiScreen<TextureProcessorSelectScreen> implements CommonComponents {
    private Consumer<TextureProcessor> onProcessorSelected = processor -> {};

    /**
     * Sets a listener that will be called when a texture processor is selected.
     *
     * @param onProcessorSelected the listener to call when a texture processor is selected
     * @return this TextureProcessorSelectScreen instance for method chaining
     */
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
                islandHeader(l("Select Texture Processor")),
                darkBoxOutlined()
                    .maxWidth(200)
                    .padding(8)
                    .children(
                        processorButton(
                            t("clicksigns.texture.processor.tiler"),
                            new Tiler(2, 16, 16)
                        ),
                        processorButton(
                            t("clicksigns.texture.processor.replaceColor"),
                            new ReplaceColor("", "green")
                        )
                    )
            );
    }

    private UiElement<?> processorButton(Component label, TextureProcessor processor) {
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
                text(label)
            );
    }
}
