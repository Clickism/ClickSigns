package de.clickism.clicksigns.ui.screen.texture.processor;

import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.Rotate;
import de.clickism.clicksigns.ui.components.NumberControl;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiElement;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class RotateControls extends ProcessorControls<Rotate, RotateControls> {
    public RotateControls(EditableTextureSource textureSource, ColorResolver colorResolver, Editable<TextureProcessor> processor) {
        super(textureSource, colorResolver, processor);
    }

    @Override
    protected UiElement<?> controls() {
        return controls(
            withHeader(
                t("clicksigns.texture.processor.rotate.degrees"),
                new NumberControl()
                    .minValue(0)
                    .maxValue(360)
                    .changeAmount(15)
                    .fineChangeAmount(1)
                    .value(processor.current().degrees())
                    .onValueChanged(degrees -> {
                        textureSource.updateProcessor(
                            processor.id(),
                            p -> ((Rotate) p).withDegrees(degrees)
                        );
                    })
            )
        );
    }
}
