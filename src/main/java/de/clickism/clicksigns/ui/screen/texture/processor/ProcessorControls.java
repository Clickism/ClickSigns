package de.clickism.clicksigns.ui.screen.texture.processor;

import com.google.common.base.CaseFormat;
import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public abstract class ProcessorControls<P extends TextureProcessor, S extends UiComponent<S>> extends UiComponent<S>
    implements CommonComponents {

    protected final EditableTextureSource textureSource;
    protected final ColorResolver colorResolver;
    protected final Editable<P> processor;

    @SuppressWarnings("unchecked")
    public ProcessorControls(EditableTextureSource textureSource, ColorResolver colorResolver, Editable<TextureProcessor> processor) {
        this.textureSource = textureSource;
        this.colorResolver = colorResolver;
        this.processor = Editable.of(processor.id(), (P) processor.current());
    }

    @Override
    protected void build() {
        grow();
        children(
            withHeader(
                nameComponent(),
                controls()
            )
        );
    }

    protected String name() {
        var name = processor.current().getClass().getSimpleName();
        // Convert to lower case
        return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL).convert(name);
    }

    private Component nameComponent() {
        return t("clicksigns.texture.processor." + name(), ChatFormatting.BOLD);
    }

    protected abstract UiElement<?> controls();

    protected UiElement<?> controls(UiElement<?>... controls) {
        return box()
            .grow()
            .childGap(4)
            .horizontal()
            .children(controls);
    }
}
