package de.clickism.clicksigns.ui.screen.texture.processor;

import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.Flip;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiElement;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class FlipControls extends ProcessorControls<Flip, FlipControls> {
    public FlipControls(EditableTextureSource textureSource, ColorResolver colorResolver, Editable<TextureProcessor> processor) {
        super(textureSource, colorResolver, processor);
    }

    @Override
    protected UiElement<?> controls() {
        return controls(
            checkboxWithText(
                t("clicksigns.texture.processor.flip.horizontal"),
                processor.current().flipX(),
                checked -> {
                    textureSource.updateProcessor(
                        processor.id(),
                        p -> ((Flip) p).withFlipX(checked)
                    );
                }
            ),
            checkboxWithText(
                t("clicksigns.texture.processor.flip.vertical"),
                processor.current().flipY(),
                checked -> {
                    textureSource.updateProcessor(
                        processor.id(),
                        p -> ((Flip) p).withFlipY(checked)
                    );
                }
            )
//            withHeader(
//                t("clicksigns.texture.processor.flip.horizontal"),
//                checkbox()
//                    .size(14)
//                    .checked(processor.current().flipX())
//                    .onCheckedChange(checked -> {
//                        textureSource.updateProcessor(
//                            processor.id(),
//                            p -> ((Flip) p).withFlipX(checked)
//                        );
//                    })
//            ),
//            withHeader(
//                t("clicksigns.texture.processor.flip.vertical"),
//                checkbox()
//                    .size(14)
//                    .checked(processor.current().flipY())
//                    .onCheckedChange(checked -> {
//                        textureSource.updateProcessor(
//                            processor.id(),
//                            p -> ((Flip) p).withFlipY(checked)
//                        );
//                    })
//            )
        );
    }
}
