package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.components.ColorField;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.components.NumberControl;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.UiScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;

public class TextureEditScreen extends UiScreen<TextureEditScreen> implements CommonComponents {
    private final EditableTextureSource textureSource;
    private final ColorResolver colorResolver;

    private Consumer<TextureSource> onTextureEdited = source -> {};

    public TextureEditScreen(TextureSource textureSource, ColorResolver colorResolver) {
        this.textureSource = new EditableTextureSource(textureSource);
        this.textureSource.onTextureSourceChanged(this::invalidateTree);
        this.colorResolver = colorResolver;
    }

    public TextureEditScreen onTextureEdited(Consumer<TextureSource> onTextureEdited) {
        this.onTextureEdited = onTextureEdited;
        return this;
    }

    @Override
    protected void build() {
        grow();
        alignCenter();
        children(
            box()
                .padding(8)
                .alignCenter()
                .style(style()
                    .borderColor(UiColor.LIGHT_GRAY.alpha(0.5f))
                    .backgroundColor(UiColor.BLACK_A50))
                .children(
                    fancyHeader(l("Texture Pipeline Editor")),

                    box()
                        .childGap(8)
                        .horizontal()
                        .children(
                            // Left - Base Texture
                            box()
                                .width(64)
                                .childGap(4)
                                .children(
                                    smallHeader(l("Base Texture")),
                                    UiUtil.imageOf(TextureSource.ofStatic(textureSource.base()).resolve(ColorResolver.empty()))
                                        .keepAspectRatio(true)
                                        .grow(),
                                    darkBox()
                                        .children(
                                            smallParagraph(l(textureSource.base().toString()))
                                        )
                                ),

                            // Right - Processors
                            box()
                                .children(
                                    smallHeader(l("Processors")),
                                    processorList(),
                                    button(l("+ Add Processor"))
                                        .growWidth()
                                        .buttonColor(UiColor.LIME)
                                        .onClick(event -> {
                                            new TextureProcessorSelectScreen()
                                                .onProcessorSelected(textureSource::addProcessor)
                                                .open();
                                        })
                                )
                        ),

                    button(ComponentUtil.confirmWithIcon())
                        .growWidth()
                        .buttonColor(UiColor.LIME)
                        .onClick(event -> {
                            onTextureEdited.accept(textureSource.build());
                            close();
                        })
                )
        );
    }

    private UiElement<?> processorList() {
        var list = box()
            .width(240)
            .childGap(4)
            .padding(4)
            .growHeight()
            .scrollable(true);
        var partialProcessors = new ArrayList<TextureProcessor>();
        for (var processor : textureSource.processors()) {
            partialProcessors.add(processor.current());
            var partialSource = new TextureSource(
                textureSource.base(),
                List.copyOf(partialProcessors)
            );
            var partialTexture = partialSource.resolve(colorResolver);
            list.add(darkBoxOutlined()
                .growWidth()
                .horizontal()
                .alignCenter()
                .childGap(4)
                .children(
                    // Preview
                    UiUtil.imageOf(partialTexture)
                        .size(32)
                        .keepAspectRatio(true),
                    // Processor controls
                    processorView(processor),
                    // Remove button
                    button("✖")
                        .buttonColor(UiColor.RED)
                        .onClick(event -> {
                            textureSource.removeProcessor(processor);
                        })
                ));
        }
        return list;
    }

    private UiElement<?> processorView(Editable<TextureProcessor> processor) {
        var current = processor.current();
        if (current instanceof Tiler tiler) {
            return box()
                .growWidth()
                .childGap(4)
                .children(
                    smallHeader(l("Tiler")).padding(0),
                    box()
                        .growWidth()
                        .horizontal()
                        .childGap(4)
                        .children(
                            box()
                                .growWidth()
                                .children(
                                    smallHeader(l("Corner Size")).padding(0),
                                    new NumberControl()
                                        .value(tiler.cornerSize())
                                        .onValueChanged(newValue -> {
                                            textureSource.updateProcessor(
                                                processor.id(),
                                                p -> ((Tiler) p).withCornerSize(newValue)
                                            );
                                        })
                                ),
                            box()
                                .growWidth()
                                .children(
                                    smallHeader(l("Width")).padding(0),
                                    new NumberControl()
                                        .value(tiler.outputWidth())
                                        .onValueChanged(newValue -> {
                                            textureSource.updateProcessor(
                                                processor.id(),
                                                p -> ((Tiler) p).withOutputSize(newValue, ((Tiler) p).outputHeight())
                                            );
                                        })
                                ),
                            box()
                                .growWidth()
                                .children(
                                    smallHeader(l("Height")).padding(0),
                                    new NumberControl()
                                        .value(tiler.outputHeight())
                                        .onValueChanged(newValue -> {
                                            textureSource.updateProcessor(
                                                processor.id(),
                                                p -> ((Tiler) p).withOutputSize(((Tiler) p).outputWidth(), newValue)
                                            );
                                        })
                                )
                        )
                );
        } else if (current instanceof ReplaceColor replaceColor) {
            return box()
                .growWidth()
                .childGap(4)
                .children(
                    smallHeader(l("Replace Color")).padding(0),
                    box()
                        .growWidth()
                        .horizontal()
                        .childGap(4)
                        .children(
                            box()
                                .growWidth()
                                .children(
                                    smallHeader(l("From")).padding(0),
                                    new ColorField(colorResolver)
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
                            box()
                                .growWidth()
                                .children(
                                    smallHeader(l("To")).padding(0),
                                    new ColorField(colorResolver)
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
                        )
                );
        } else {
            return box()
                .growWidth()
                .children(
                    smallHeader(l("Unknown Processor"))
                );
        }
    }

}
