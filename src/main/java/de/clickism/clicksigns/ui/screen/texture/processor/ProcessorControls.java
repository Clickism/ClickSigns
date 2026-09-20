package de.clickism.clicksigns.ui.screen.texture.processor;

import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.processors.*;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.EditableTextureSource;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

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

    protected Component name() {
        return processor.current().translatedName();
    }

    private Component nameComponent() {
        return name().copy().withStyle(ChatFormatting.BOLD);
    }

    protected abstract UiElement<?> controls();

    protected UiElement<?> controls(UiElement<?>... controls) {
        return box()
            .grow()
            .childGap(4)
            .horizontal()
            .children(controls);
    }

    /**
     * Creates the appropriate controls for a given texture processor based on its type.
     *
     * @param processor the editable texture processor for which to create controls
     * @return the UI element containing the controls for the specified texture processor
     */
    public static UiElement<?> create(
        Editable<TextureProcessor> processor,
        EditableTextureSource textureSource,
        ColorResolver colorResolver
    ) {
        var current = processor.current();
        return switch (current.typeKey()) {
            case Tiler.TYPE -> new TilerControls(textureSource, colorResolver, processor);
            case ReplaceColor.TYPE -> new ReplaceColorControls(textureSource, colorResolver, processor);
            case AlphaMask.TYPE -> new AlphaMaskControls(textureSource, colorResolver, processor);
            case Rotate.TYPE -> new RotateControls(textureSource, colorResolver, processor);
            case Flip.TYPE -> new FlipControls(textureSource, colorResolver, processor);
            default -> new UnknownControls(textureSource, colorResolver, processor);
        };
    }
}
