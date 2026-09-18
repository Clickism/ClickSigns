package de.clickism.clicksigns.ui.screen.texture.processor;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiElement;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class UnknownControls extends ProcessorControls<TextureProcessor, UnknownControls> {
    public UnknownControls(EditableTextureSource textureSource, ColorResolver colorResolver, Editable<TextureProcessor> processor) {
        super(textureSource, colorResolver, processor);
    }

    @Override
    protected UiElement<?> controls() {
        return smallHeader(t("clicksigns.texture.processor.unknown.description"));
    }

    @Override
    protected String name() {
        return t("clicksigns.texture.processor.unknown").getString();
    }
}
