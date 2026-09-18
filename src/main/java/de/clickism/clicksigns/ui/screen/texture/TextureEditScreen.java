package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureProcessor;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.processors.AlphaMask;
import de.clickism.clicksigns.sign.texture.source.processors.ReplaceColor;
import de.clickism.clicksigns.sign.texture.source.processors.Tiler;
import de.clickism.clicksigns.ui.components.ColorField;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.components.NumberControl;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.style.Border;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;

// TODO: Translate, clean up
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
        var inputSource = TextureSource.ofStatic(textureSource.base());
        var inputTexture = inputSource.resolve(colorResolver);
        var outputSource = textureSource.build();
        var outputTexture = outputSource.resolve(colorResolver);
        this.alignCenter()
            .grow()
            .padding(8)
            .childGap(8)
            .children(
                h4(l("Texture Pipeline Editor"))
                    .padding(6, 12)
                    .style(style()
                        .borderColor(UiColor.LIGHT_GRAY.alpha(0.5f))
                        .backgroundColor(UiColor.BLACK.alpha(0.5f))),

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
                                        fancyHeader(l("Base Texture")),
                                        new ImageWithPicker(inputSource, colorResolver, 112)
                                            .growWidth(),

                                        withHeader(
                                            l("Texture Size"),
                                            darkBox()
                                                .children(
                                                    smallParagraph(l(inputTexture.width() + "x" + inputTexture.height()))
                                                        .alignTextCenter()
                                                )
                                        ),

                                        withHeader(
                                            l("Resource Path"),
                                            darkBox()
                                                .children(
                                                    smallParagraph(l(textureSource.base().toString()))
                                                )
                                        )
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
                                fancyHeader(l("Texture Processors")),
                                processorList().growHeight(),

                                button(l("+ Add Processor"))
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
                                        fancyHeader(l("Output Texture")),
                                        new ImageWithPicker(outputSource, colorResolver, 112)
                                            .growWidth(),

                                        withHeader(
                                            l("Texture Size"),
                                            darkBox()
                                                .children(
                                                    smallParagraph(l(outputTexture.width() + "x" + outputTexture.height()))
                                                        .alignTextCenter()
                                                )
                                        ),

                                        withHeader(
                                            l("Resource Path"),
                                            darkBox()
                                                .children(
                                                    smallParagraph(l(outputTexture.location().toString()))
                                                )
                                        ),

                                        box().grow(),

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

    private UiElement<?> processorList() {
        var list = box()
            .grow()
            .padding(0, 6)
            .childGap(4)
            .scrollable(true);
        var partialProcessors = new ArrayList<TextureProcessor>();
        for (var processor : textureSource.processors()) {
            partialProcessors.add(processor.current());
            var partialSource = new TextureSource(
                textureSource.base(),
                List.copyOf(partialProcessors)
            );
            var view = memo(processor.id() + "-view", () -> processorView(processor));
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
                    view,
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

    private UiElement<?> processorView(Editable<TextureProcessor> processor) {
        var current = processor.current();
        if (current instanceof Tiler tiler) {
            return withHeader(
                l("Tiler").copy().withStyle(ChatFormatting.BOLD),
                box()
                    .growWidth()
                    .horizontal()
                    .childGap(4)
                    .children(
                        withHeader(
                            l("Corner Size"),
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
                            l("Width"),
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
                            l("Height"),
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
                    )
            );
        } else if (current instanceof ReplaceColor replaceColor) {
            return box()
                .growWidth()
                .childGap(4)
                .children(
                    smallHeader(l("Replace Color").copy().withStyle(ChatFormatting.BOLD)).padding(0),
                    box()
                        .growWidth()
                        .horizontal()
                        .childGap(4)
                        .children(
                            withHeader(
                                l("From (Optional)"),
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
                                l("To"),
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
                        )
                );
        } else if (current instanceof AlphaMask) {
            return box()
                .grow()
                .childGap(4)
                .children(
                    smallHeader(l("Alpha Mask").copy().withStyle(ChatFormatting.BOLD)).padding(0),
                    darkBox()
                        .grow()
                        .children(
                            smallParagraph(l("Used for matching back textures with front textures. Not editable."))
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
