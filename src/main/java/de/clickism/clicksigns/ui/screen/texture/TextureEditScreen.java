package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.processors.AlphaMask;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.components.ImageWithPicker;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.processor.AlphaMaskControls;
import de.clickism.clicksigns.ui.screen.texture.processor.ReplaceColorControls;
import de.clickism.clicksigns.ui.screen.texture.processor.TilerControls;
import de.clickism.clicksigns.ui.screen.texture.processor.UnknownControls;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.style.Border;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A screen for editing a texture source, allowing users to modify the base texture and apply various texture processors.
 */
public class TextureEditScreen extends UiScreen<TextureEditScreen> implements CommonComponents {
    private final EditableTextureSource textureSource;
    private final ColorResolver colorResolver;

    private Consumer<TextureSource> onTextureEdited = source -> {};

    /**
     * Constructs a new TextureEditScreen with the specified texture source and color resolver.
     *
     * @param textureSource the texture source to edit
     * @param colorResolver the color resolver to use for resolving colors in the texture
     */
    public TextureEditScreen(TextureSource textureSource, ColorResolver colorResolver) {
        this.textureSource = new EditableTextureSource(textureSource);
        this.textureSource.onTextureSourceChanged(this::invalidateTree);
        this.colorResolver = colorResolver;
    }

    /**
     * Sets a listener that will be called when the texture has been edited and confirmed.
     *
     * @param onTextureEdited the listener to call when the texture has been edited
     * @return this TextureEditScreen instance for method chaining
     */
    public TextureEditScreen onTextureEdited(Consumer<TextureSource> onTextureEdited) {
        this.onTextureEdited = onTextureEdited;
        return this;
    }

    @Override
    protected void build() {
        var inputSource = TextureSource.ofStatic(textureSource.base());
        var outputSource = textureSource.build();
        this.alignCenter()
            .grow()
            .padding(8)
            .childGap(8)
            .children(
                islandHeader(t("clicksigns.texture.editor.header")),
                box()
                    .horizontal()
                    .childGap(8)
                    .growWidth()
                    .children(
                        // Left - Base Texture and properties
                        box()
                            .grow()
                            .crossAlign(Align.END)
                            .children(
                                darkBoxOutlined()
                                    .maxWidth(120)
                                    .childGap(8)
                                    .growHeight()
                                    .children(
                                        fancyHeader(t("clicksigns.texture.editor.base.header")),
                                        // Input image
                                        new ImageWithPicker(inputSource, colorResolver, 112)
                                            .growWidth(),

                                        textureInfo(inputSource, false)
                                    )
                            ),
                        // Center - Texture Processors
                        darkBoxOutlined()
                            .scrollable(false)
                            .grow()
                            .minHeight(300)
                            .maxHeight(400)
                            .maxWidth(300)
                            .childGap(8)
                            .children(
                                fancyHeader(t("clicksigns.texture.editor.processors.headers")),
                                processorList().growHeight(),

                                button(t("+", "clicksigns.texture.editor.processors.add"))
                                    .disabled(textureSource.processors().size() >= TextureSource.MAX_PROCESSOR_COUNT)
                                    .growWidth()
                                    .buttonColor(UiColor.TEAL)
                                    .onClick(event -> {
                                        new TextureProcessorSelectScreen()
                                            .onProcessorSelected(textureSource::addProcessor)
                                            .open();
                                    })
                            ),

                        // Right - Output preview and confirm button
                        box()
                            .grow()
                            .children(
                                darkBoxOutlined()
                                    .grow()
                                    .maxWidth(120)
                                    .childGap(8)
                                    .children(
                                        fancyHeader(t("clicksigns.texture.editor.output.header")),
                                        // Output image
                                        new ImageWithPicker(outputSource, colorResolver, 112)
                                            .growWidth(),

                                        textureInfo(outputSource, true),

                                        box().grow(), // Spacer
                                        button(ComponentUtil.confirmWithIcon())
                                            .growWidth()
                                            .buttonColor(UiColor.LIME)
                                            .onClick(event -> {
                                                onTextureEdited.accept(textureSource.build());
                                                close();
                                            })
                                    )
                            )
                    )
            );
    }

    /**
     * Creates a UI element that displays information about a texture, including its size and path.
     *
     * @param source           the texture source to display information for
     * @param showResolvedPath whether to show the resolved path or the base path of the texture
     * @return the UI element containing the texture information
     */
    private UiElement<?> textureInfo(TextureSource source, boolean showResolvedPath) {
        var texture = source.resolve(colorResolver);
        var path = showResolvedPath
            ? texture.location().toString()
            : source.base().toString();
        return box()
            .growWidth()
            .childGap(8)
            .children(
                withHeader(
                    t("clicksigns.texture.editor.textureInfo.size"),
                    darkBox()
                        .children(
                            smallParagraph(l(texture.width() + " x " + texture.height()))
                                .alignTextCenter()
                        )
                ),
                withHeader(
                    t("clicksigns.texture.editor.textureInfo.path"),
                    darkBox()
                        .children(
                            smallParagraph(l(path))
                        )
                )
            );
    }

    /**
     * Creates a list of processors with their controls and buttons to move or remove them.
     *
     * @return the UI element containing the list of processors
     */
    private UiElement<?> processorList() {
        var list = box()
            .grow()
            .padding(0, 6)
            .childGap(4)
            .scrollable(true);
        // Keep track of processors until the current one
        var partialProcessors = new ArrayList<TextureProcessor>();
        for (var processor : textureSource.processors()) {
            // Update partial processors
            partialProcessors.add(processor.current());
            var partialSource = new TextureSource(
                textureSource.base(),
                List.copyOf(partialProcessors)
            );
            // Memoize the processor controls
            var controls = memo(processor.id() + "-view", () -> processorControls(processor));
            // Add processor entry
            list.add(darkBoxOutlined()
                .style(style()
                    .borderPosition(Border.Position.INSIDE)
                    .backgroundColor(UiColor.BLACK_A80))
                .growWidth()
                .horizontal()
                .alignCenter()
                .childGap(4)
                .height(53)
                .padding(4)
                .children(
                    // Preview
                    new ImageWithPicker(partialSource, colorResolver, 45)
                        .size(45),
                    // Processor controls
                    controls,
                    // Buttons
                    box()
                        .growHeight()
                        .alignCenter()
                        .childGap(2)
                        .children(
                            button("⬆")
                                .size(12)
                                .buttonColor(UiColor.LIGHT_GRAY)
                                .onClick(event -> {
                                    textureSource.moveProcessor(processor.id(), -1);
                                }),
                            box().grow(),
                            // Remove button
                            button("🗑")
                                .size(12)
                                .buttonColor(UiColor.RED)
                                .onClick(event -> {
                                    textureSource.removeProcessor(processor);
                                }),
                            box().grow(),
                            button("⬇")
                                .size(12)
                                .buttonColor(UiColor.LIGHT_GRAY)
                                .onClick(event -> {
                                    textureSource.moveProcessor(processor.id(), 1);
                                })
                        )
                ));
        }
        return list;
    }

    /**
     * Creates the appropriate controls for a given texture processor based on its type.
     *
     * @param processor the editable texture processor for which to create controls
     * @return the UI element containing the controls for the specified texture processor
     */
    private UiElement<?> processorControls(Editable<TextureProcessor> processor) {
        var current = processor.current();
        return switch (current.typeKey()) {
            case Tiler.TYPE -> new TilerControls(textureSource, colorResolver, processor);
            case ReplaceColor.TYPE -> new ReplaceColorControls(textureSource, colorResolver, processor);
            case AlphaMask.TYPE -> new AlphaMaskControls(textureSource, colorResolver, processor);
            default -> new UnknownControls(textureSource, colorResolver, processor);
        };
    }
}
