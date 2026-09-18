package de.clickism.clicksigns.ui.screen.texture.processor;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.ui.components.ColorField;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiElement;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class ReplaceColorControls extends ProcessorControls<ReplaceColor, ReplaceColorControls> {
    public ReplaceColorControls(EditableTextureSource textureSource, ColorResolver colorResolver, Editable<TextureProcessor> processor) {
        super(textureSource, colorResolver, processor);
    }

    @Override
    protected UiElement<?> controls() {
        var replaceColor = processor.current();
        return controls(
            withHeader(
                t("clicksigns.texture.processor.replaceColor.from"),
                new ColorField(colorResolver)
                    .growWidth()
                    .height(14)
                    .padding(2, 4)
                    .value(replaceColor.fromColor() != null
                        ? replaceColor.fromColor()
                        : "")
                    .onColorChanged(color -> {
                        var newColor = color.isEmpty()
                            ? null
                            : color;
                        textureSource.updateProcessor(
                            processor.id(),
                            p -> ((ReplaceColor) p).withFromColor(newColor)
                        );
                    })
            ),
            withHeader(
                t("clicksigns.texture.processor.replaceColor.to"),
                new ColorField(colorResolver)
                    .growWidth()
                    .height(14)
                    .padding(2, 4)
                    .value(replaceColor.toColor())
                    .onColorChanged(color -> {
                        if (color.isEmpty()) {
                            return;
                        }
                        textureSource.updateProcessor(
                            processor.id(),
                            p -> ((ReplaceColor) p).withToColor(color)
                        );
                    })
            )
        );
    }
}
