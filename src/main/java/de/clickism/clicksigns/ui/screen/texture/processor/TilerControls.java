package de.clickism.clicksigns.ui.screen.texture.processor;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.components.NumberControl;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiElement;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TilerControls extends ProcessorControls<Tiler, TilerControls> {
    public TilerControls(EditableTextureSource textureSource, ColorResolver colorResolver, Editable<TextureProcessor> processor) {
        super(textureSource, colorResolver, processor);
    }

    @Override
    protected UiElement<?> controls() {
        var tiler = processor.current();
        return controls(
            withHeader(
                t("clicksigns.texture.processor.cornerSize"),
                new NumberControl()
                    .value(tiler.cornerSize())
                    .minValue(0)
                    .onValueChanged(newValue -> {
                        textureSource.updateProcessor(
                            processor.id(),
                            p -> ((Tiler) p).withCornerSize(newValue)
                        );
                    })
            ),
            withHeader(
                t("clicksigns.texture.processor.width"),
                new NumberControl()
                    .value(tiler.outputWidth())
                    .fastChangeAmount(8)
                    .minValue(RoadSign.MIN_SIGN_SIZE.width())
                    .maxValue(RoadSign.MAX_SIGN_SIZE.width())
                    .onValueChanged(newValue -> {
                        textureSource.updateProcessor(
                            processor.id(),
                            p -> ((Tiler) p).withOutputSize(newValue, ((Tiler) p).outputHeight())
                        );
                    })
            ),
            withHeader(
                t("clicksigns.texture.processor.width"),
                new NumberControl()
                    .value(tiler.outputHeight())
                    .fastChangeAmount(8)
                    .minValue(RoadSign.MIN_SIGN_SIZE.height())
                    .maxValue(RoadSign.MAX_SIGN_SIZE.height())
                    .onValueChanged(newValue -> {
                        textureSource.updateProcessor(
                            processor.id(),
                            p -> ((Tiler) p).withOutputSize(((Tiler) p).outputWidth(), newValue)
                        );
                    })
            )
        );
    }
}
