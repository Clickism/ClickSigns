package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.element.*;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.components.*;
import de.clickism.clicksigns.ui.components.sign.SymbolView;
import de.clickism.clicksigns.ui.screen.texture.TextureEditScreen;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.layout.Align;
import net.minecraft.network.chat.Component;

import java.util.UUID;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * The element controls, meant for editing the selected element.
 */
class SignElementControls extends UiComponent<SignElementControls> implements CommonComponents {
    private final SignEditorContext context;

    public SignElementControls(SignEditorContext context) {
        this.context = context;
    }

    @Override
    protected void build() {
        childGap(4);

        var editableElement = context.selected();
        if (editableElement == null) {
            addInfo();
            return;
        }
        add(fancyHeader(t("clicksigns.editor.element.header")));

        // Text controls
        // TODO: Fix in ClickUI, text fields scissor breaks when scrolling
        var element = editableElement.current();
        var id = editableElement.id();
        if (element instanceof TextElement text) {
            addTextControls(text, id);
        }
        if (element instanceof PlateElement plate) {
            addPlateControls(plate, id);
        }
        if (element instanceof SymbolElement symbol) {
            addSymbolControls(symbol, id);
        }

        add(box().growHeight()); // Spacer

        addCommonControls(element, id);
    }

    private void addInfo() {
        add(fancyHeader(t("clicksigns.editor.info.header")));
        add(box()
            .grow()
            .childGap(4)
            .children(
                box(), // Spacer
                // None selected
                box()
                    .growWidth()
                    .padding(8)
                    .style(style()
                        .backgroundColor(UiColor.BLACK_A20))
                    .children(
                        paragraph(t("clicksigns.editor.info.noneSelected"))
                            .alignTextCenter()
                    ),
                box(), // Spacer
                box()
                    .growWidth()
                    .padding(8)
                    .style(style()
                        .backgroundColor(UiColor.BLACK_A20))
                    .children(
                        paragraph(t("clicksigns.editor.info.noneSelected.description"))
                    ),
                box().grow(), // Spacer
                // Controls
                smallHeader(t("clicksigns.editor.info.controls.header")),
                box()
                    .growWidth()
                    .padding(4)
                    .childGap(4)
                    .style(style()
                        .backgroundColor(UiColor.BLACK_A20))
                    .children(
                        describeLeftClick(t("clicksigns.editor.info.controls.select")),
                        describeAction(action(t("clicksigns.ui.drag")),
                            t("clicksigns.editor.info.controls.move")),
                        describeAction(action(t("clicksigns.ui.ctrl"), l("C")),
                            t("clicksigns.editor.info.controls.copy")),
                        describeAction(action(t("clicksigns.ui.ctrl"), l("V")),
                            t("clicksigns.editor.info.controls.paste")),
                        describeAction(action(t("clicksigns.ui.ctrl"), l("D")),
                            t("clicksigns.editor.info.controls.duplicate")),
                        describeAction(action(t("clicksigns.ui.ctrl"), l("A")),
                            t("clicksigns.editor.info.controls.selectAll")),
                        describeAction(action(t("clicksigns.ui.delete")),
                            t("clicksigns.editor.info.controls.delete"))
                    ),
                smallHeader(l("Tips")),
                box()
                    .childGap(4)
                    .growWidth()
                    .children(
                        tip(t("clicksigns.editor.info.tips.numberControls")),
                        tip(t("clicksigns.editor.info.tips.multipleElements"))
                    )
            ));
    }

    private UiElement<?> tip(Component text) {
        return box()
            .growWidth()
            .padding(8)
            .style(style()
                .backgroundColor(UiColor.BLACK_A20))
            .children(
                smallParagraph(text)
            );
    }

    private void addTextControls(TextElement text, UUID id) {
        add(smallHeader(t("clicksigns.editor.element.text.textColor")));
        var colorResolver = context.roadSign().colorResolver();
        // Foreground color
        var style = text.style();
        add(memo(
            id + "-text-color",
            () -> new ColorField(colorResolver)
                .value(style.color()))
            .onColorChanged(newColor -> {
                context.roadSign().updateTextElement(id, element ->
                    element.withStyle(s -> s.withColor(newColor)));
            }));

        // Background color
        add(smallHeader(t("clicksigns.editor.element.text.backgroundColor")).padding(0));
        add(memo(
            id + "-bg-color",
            () -> new ColorField(colorResolver)
                .value(style.backgroundColor().orElse("")))
            .onColorChanged(newColor -> {
                context.roadSign().updateTextElement(id, element ->
                    element.withStyle(s -> s.withBackgroundColor(newColor)));
            }));
        add(smallHeader(t("clicksigns.editor.element.text.outlineColor")));
        // Outline color
        add(memo(
            id + "-outline-color",
            () -> new ColorField(colorResolver)
                .value(style.outlineColor().orElse("")))
            .onColorChanged(newColor -> {
                context.roadSign().updateTextElement(id, element ->
                    element.withStyle(s -> s.withOutlineColor(newColor)));
            }));

        // Outline Width
        if (style.outlineColor().isPresent()) {
            add(smallHeader(t("clicksigns.editor.element.text.outlineWidth")).padding(0));
            add(memo(id + "-outline-width", () -> new NumberControl()
                .value(style.outlineWidth())
                .unit(l("pt"))
                .minValue(1) // Don't allow 0
                .maxValue(TextStyle.MAX_OUTLINE_WIDTH)
                .onValueChanged(newWidth -> {
                    context.roadSign().updateTextElement(id, element ->
                        element.withStyle(s -> s.withOutlineWidth(newWidth)));
                })
            ));
        }

        // Padding
        if (style.isPaddingShown()) {
            add(smallHeader(t("clicksigns.editor.element.text.padding")));
            add(memo(id + "-padding", () -> new SizeControls(new Size(style.paddingX(), style.paddingY()))
                .minSize(new Size(0, 0))
                .maxSize(new Size(TextStyle.MAX_PADDING, TextStyle.MAX_PADDING))
                .widthHeader(t("clicksigns.editor.element.text.padding.horizontal"))
                .heightHeader(t("clicksigns.editor.element.text.padding.vertical"))
                .unit(l("pt"))
                .changeAmount(1)
                .fineChangeAmount(0)
                .onSizeChanged(newPadding -> {
                    context.roadSign().updateTextElement(id,
                        element -> element.withStyle(s -> s
                            .withPaddingX(newPadding.width())
                            .withPaddingY(newPadding.height())));
                })));
        }

        // Scale
        add(smallHeader(t("clicksigns.editor.element.text.fontSize")));
        add(memo(id + "-font-size", () -> new NumberControl()
            .unit(l("pt"))
            .changeAmount(1)
            .fastChangeAmount(4)
            .minValue(TextElement.MIN_TEXT_PT)
            .maxValue(TextElement.MAX_TEXT_PT)
            .value((int) (text.scale() * 9f)) // Convert scale to pt
            .onValueChanged(newPt -> {
                var newScale = ((float) newPt) / 9f; // Convert pt to scale
                context.roadSign().updateTextElement(id, element ->
                    element.withScale(newScale));
            })
        ));

        if (text.lines().size() > 1) {
            // Line Controls
            add(smallHeader(t("clicksigns.editor.element.text.lineAlignment")).padding(0));
            add(memo(id + "-line-alignment", () -> new AlignmentSelector()
                .alignment(switch (text.style().textAlignment()) {
                    case LEFT -> Alignment.TEXT_LEFT;
                    case CENTER -> Alignment.TEXT_CENTER;
                    case RIGHT -> Alignment.TEXT_RIGHT;
                })
                .textOnly(true)
                .onAlignmentChange(newAlignment -> {
                    context.roadSign().updateTextElement(id, element ->
                        element.withStyle(s -> s
                            .withTextAlignment(switch (newAlignment) {
                                case TOP_LEFT -> TextStyle.TextAlignment.LEFT;
                                case TOP_CENTER -> TextStyle.TextAlignment.CENTER;
                                case TOP_RIGHT -> TextStyle.TextAlignment.RIGHT;
                                default -> s.textAlignment();
                            })));
                })
            ));

            add(smallHeader(t("clicksigns.editor.element.text.lineGap")).padding(0));
            add(memo(id + "-line-gap", () -> new NumberControl()
                .unit(l("pt"))
                .changeAmount(1)
                .fastChangeAmount(4)
                .minValue(0)
                .maxValue(20)
                .value(text.style().lineGap())
                .onValueChanged(newGap -> {
                    context.roadSign().updateTextElement(id, element ->
                        element.withStyle(s -> s.withLineGap(newGap)));
                })
            ));
        }
    }

    private void addPlateControls(PlateElement plate, UUID id) {
        add(smallHeader(t("clicksigns.editor.element.plate.textures")));

        var roadSign = context.roadSign();
        add(box()
            .horizontal()
            .growWidth()
            .crossAlign(Align.CENTER)
            .childGap(4)
            .padding(2)
            .style(style()
                .backgroundColor(UiColor.BLACK_A20))
            .children(
                checkbox()
                    .checked(plate.matchSignTextures())
                    .onCheckedChange(checked -> {
                        roadSign.updatePlateElement(id, element ->
                            element
                                // Make sure textures match again
                                .withFrontSource(roadSign.frontSource().resize(element.size()))
                                .withBackSource(roadSign.backSource().resize(element.size()))
                                .withMatchSignTextures(checked));
                    }),
                text(t("clicksigns.editor.element.plate.matchSignTextures"))
                    .style(style()
                        .fontScale(0.8f)
                        .alpha(0.8f)
                    )));

        if (!plate.matchSignTextures()) {
            // Show texture options
            add(new TwoSidedTextureButton(plate.frontSource(), plate.backSource(), roadSign.colorResolver())
                .onFrontSelected(source -> {
                    roadSign.updatePlateElement(id, element ->
                        element.withFrontSource(source.resize(element.size())));
                })
                .onBackSelected(source -> {
                    roadSign.updatePlateElement(id, element ->
                        element.withBackSource(source.resize(element.size())));
                })
                .maskBack());
        }

        add(smallHeader(t("clicksigns.editor.element.plate.size")));
        add(memo(id + "-plate-size", () -> new SizeControls(plate.size())
            .minSize(PlateElement.MIN_PLATE_SIZE)
            .maxSize(PlateElement.MAX_PLATE_SIZE)
            .changeAmount(8)
            .fineChangeAmount(1)
            .allowInput(plate.frontSource().isResizable())
            .onSizeChanged(newSize -> {
                context.roadSign().updatePlateElement(id,
                    element -> {
                        var newFront = element.frontSource().resize(newSize);
                        var newBack = element.backSource().resize(newSize);
                        return element.withFrontSource(newFront).withBackSource(newBack);
                    });
            })
        ));
    }

    private void addSymbolControls(SymbolElement symbol, UUID id) {
        add(smallHeader(t("clicksigns.editor.element.symbol.symbol")));
        // TODO: Texture edit screen
        var roadSign = context.roadSign();
        var colorResolver = roadSign.colorResolver();
        add(new SymbolView(symbol, colorResolver)
            .padding(4)
            .style(style()
                .borderColor(UiColor.GRAY)
                .backgroundColor(UiUtil.primaryColorOf(roadSign.frontSource().resolveImage(colorResolver)))
                .whenHovered(style()
                    .borderColor(UiColor.RED)))
            .tooltip(descriptions(
                describeLeftClick(t("clicksigns.overview.symbol.tooltip.leftClick")),
                describeRightClick(t("clicksigns.overview.symbol.tooltip.rightClick"))
            ))
            .onClick(event -> {
                event.playSound();
                SymbolView.handleSymbolChange(roadSign, roadSign.getElement(id), event);
            }));
        add(button("Edit")
            .onClick(event -> {
                new TextureEditScreen(symbol.symbol().texture(), colorResolver).open();
            }));
    }

    private void addCommonControls(SignElement element, UUID id) {
        // Add Alignment
        add(smallHeader(t("clicksigns.editor.element.general.alignment")));
        add(memo(id + "-alignment", () -> new AlignmentSelector()
            .alignment(element.alignment())
            .onAlignmentChange(newAlignment -> {
                context.roadSign().updateElement(id,
                    el -> el.withAlignment(newAlignment));
            })));

        // Delete button
        add(smallHeader(t("clicksigns.editor.element.other.header")));
        add(button(t("🗑", "clicksigns.editor.element.other.removeElement"))
            .growWidth()
            .buttonColor(UiColor.MAROON)
            .onClick(event -> {
                context.roadSign().removeElement(id);
            }));
    }
}
