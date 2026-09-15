package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.element.TextStyle;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.editor.EditableSignElement;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.ui.elements.SymbolView;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.*;
import de.clickism.clickui.elements.Box;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.layout.Point;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

// TODO: Split into different classes
// TODO: Info button instead of too many tooltips
// TODO: Ability to select multiple elements and move them together/copy etc.
public class SignEditScreen extends UiScreen<SignEditScreen>
    implements FancyHeaders {

    private static final Size MIN_SIGN_SIZE = new Size(6, 6);
    private static final Size MAX_SIGN_SIZE = new Size(144, 144); // 9 Blocks

    private static final Size MIN_PLATE_SIZE = new Size(4, 4);
    private static final Size MAX_PLATE_SIZE = MAX_SIGN_SIZE;

    private static final int MIN_TEXT_PT = 3;
    private static final int MAX_TEXT_PT = 72;

    private static final int MAX_OUTLINE_WIDTH = 10;
    private static final int MAX_PADDING = 20;

    private final EditableRoadSign sign;
    private final Ref<SignView> signViewRef = ref();
    private final Ref<SignControls> signControlsRef = ref();
    private final Ref<SignEditor> signEditorRef = ref();
    private final Ref<ElementControls> elementControlsRef = ref();

    private @Nullable EditableSignElement selected = null;
    private static @Nullable EditableSignElement copied = null;

    private Consumer<RoadSign> onSignUpdate = sign -> {};

    public SignEditScreen(@NotNull RoadSign sign) {
        this.sign = new EditableRoadSign(sign);
        this.sign.onSignChanged(() -> {
            // Update the sign view and controls when the sign changes
            this.signControlsRef.get().invalidateTree();
            this.elementControlsRef.get().invalidateTree();
        });
    }

    public SignEditScreen onSignUpdate(Consumer<RoadSign> onSignUpdate) {
        this.onSignUpdate = onSignUpdate;
        return this;
    }

    private void selected(EditableSignElement element) {
        this.selected = element;
        this.elementControlsRef.get().invalidateTree();
    }

    @Override
    public void build() {
        this.grow()
            .horizontal()
            .children(
                // Left panel
                panel()
                    .children(
                        new SignControls()
                            .ref(signControlsRef)
                            .grow()
                            .crossAlign(Align.CENTER)
                    ),

                // Center editor
                box()
                    .grow()
                    .alignCenter()
                    .childGap(8)
                    .children(
                        new SignEditor()
                            .ref(signEditorRef)
                            .grow()
                            .alignCenter()
                    ),

                // Right panel
                panel()
                    .children(
                        new ElementControls()
                            .ref(elementControlsRef)
                            .grow()
                            .crossAlign(Align.CENTER)
                    )
            );
    }

    private Box panel() {
        var panelWidth = 130;
        return box()
            .scrollable(true)
            .width(panelWidth)
            .growHeight()
            .padding(8)
            .style(style()
                .backgroundColor(UiColor.BLACK_A40)
                .borderColor(UiColor.WHITE_A30));
    }

    private class SignEditor extends UiComponent<SignEditor> {

        private int dragStartX = 0;
        private int dragStartY = 0;
        private EditableSignElement dragged = null;

        private SignEditor() {
            globalEvents().onKeyPress(event -> {
                // Duplicate selected element with Ctrl+D
                if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_D) {
                    spawnElementNearSelected(selected);
                    event.consume();
                }
                // Copy selected element with Ctrl+C
                if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_C) {
                    if (selected != null) {
                        copied = selected;
                        event.consume();
                    }
                }
                // Paste copied element with Ctrl+V
                if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_V) {
                    spawnElementNearSelected(copied);
                    event.consume();
                }
                // Delete selected element with Delete key
                if (event.code() == GLFW.GLFW_KEY_DELETE) {
                    // Don't use the delete key for text elements
                    if (selected != null && !(selected.current() instanceof TextElement)) {
                        sign.removeElement(selected.id());
                        selected(null);
                        event.consume();
                    }
                }
            });
        }

        @Override
        protected void build() {
            childGap(8);
            children(box().childGap(8).grow().alignCenter().children(
                // Sign view
                box()
                    .padding(1)
                    .growWidth()
                    .alignCenter()
                    .scrollable(true)
                    .children(
                        memo(() -> new SignView(sign)
                            .ref(signViewRef)
                            .signConfig((uiElement, editable) -> {
                                uiElement.style(style()
                                    .whenHovered(style()
                                        .borderColor(UiColor.CYAN.alpha(.5f))));
                            })
                            .elementConfig((uiElement, editable) -> {
                                uiElement
                                    // Hover style
                                    .style(style()
                                        .whenHovered(style()
                                            .borderColor(UiColor.RED))
                                        .when(context -> editable.equals(selected) || editable.equals(dragged),
                                            style()
                                                .borderColor(UiColor.GREEN)
                                                .addPostRenderHook((context, el) -> {
                                                    // Render origin point of element
                                                    var signElement = editable.current();
                                                    var localOrigin = new Point(signElement.x(), signElement.y());
                                                    var origin = signViewRef.get().screenPositionOf(localOrigin);
                                                    UiUtil.renderPlusOnTop(
                                                        context.graphics(),
                                                        origin.x(),
                                                        origin.y(),
                                                        5,
                                                        UiColor.MAGENTA.color()
                                                    );
                                                })))
                                    // Update selected on click
                                    .onClick(event -> {
                                        selected(editable);
                                    })
                                    .onDragStart(event -> {
                                        dragStartX = editable.current().x();
                                        dragStartY = editable.current().y();
                                        dragged = editable;
                                        signViewRef.get().renderGuidelines(true);
                                    })
                                    // Drag controls
                                    .onDrag(event -> {
                                        // Get the delta in sign space
                                        int deltaX = (int) (event.totalDeltaX() / UI_SCALE);
                                        int deltaY = (int) (event.totalDeltaY() / UI_SCALE);

                                        int newX = dragStartX + deltaX;
                                        int newY = dragStartY - deltaY;

                                        if (dragged == null) return;

                                        var currentElement = dragged.current();
                                        if (newX == currentElement.x() && newY == currentElement.y()) {
                                            // No change
                                            return;
                                        }

                                        // Replace the element in the sign with a new one at the new position
                                        sign.updateElement(
                                            dragged.id(),
                                            element -> element.withPosition(newX, newY)
                                        );
                                    })
                                    .onDragEnd(event -> {
                                        dragged = null;
                                        signViewRef.get().renderGuidelines(false);
                                    });
                            }))
                    ),
                box()
                    // Use panel height from overview screen to keep alignment consistent
                    .height(SignOverviewScreen.PANEL_HEIGHT)
                    .children(
                        box()
                            .alignCenter()
                            .padding(4)
                            .childGap(8)
                            // Make width equivalent to 2 block signs
                            .width(32 * UI_SCALE)
                            .style(style()
                                .backgroundColor(UiColor.BLACK_A50))
                            .children(
                                // Sign controls
                                new SizeControls(new Size(sign.width(), sign.height()))
                                    .minSize(MIN_SIGN_SIZE)
                                    .maxSize(MAX_SIGN_SIZE)
                                    .changeAmount(8)
                                    .fineChangeAmount(1)
                                    .unit(l("px"))
                                    .onSizeChanged(newSize -> {
                                        sign.resize(newSize.width(), newSize.height());
                                    }),
                                // Confirm button
                                button(ComponentUtil.confirmWithIcon())
                                    .buttonColor(UiColor.LIME)
                                    .growWidth()
                                    .onClick(event -> {
                                        // Callback and close
                                        onSignUpdate.accept(sign.build());
                                        close();
                                    })
                            )
                    )
            ));
        }
    }

    /**
     * The sign controls, for editing general info about ths sign,
     * such as textures or adding elements.
     */
    private class SignControls extends UiComponent<SignControls> {
        @Override
        protected void build() {
            childGap(4);
            children(
                fancyHeader(t("clicksigns.editor.sign_properties")),
                // Add texture selection
                smallHeader(t("clicksigns.editor.sign_textures")),
                box()
                    .horizontal()
                    .growWidth()
                    .childGap(4)
                    .children(
                        box()
                            .growWidth()
                            .childGap(4)
                            .children(
                                smallHeader(l("Front")).padding(0),
                                new TextureButton(sign.frontSource(), newTexture -> {
                                    sign.frontSource(newTexture.resizeToFit(sign.build()));
                                })
                            ),
                        box()
                            .growWidth()
                            .childGap(4)
                            .children(
                                smallHeader(l("Back")).padding(0),
                                new TextureButton(sign.backSource(), newTexture -> {
                                    sign.backSource(newTexture.resizeToFit(sign.build()));
                                })
                            )
                    ),
                // Add element controls
                smallHeader(t("clicksigns.editor.elements")),

                button(t("+", "clicksigns.editor.elements.add_symbol"))
                    .growWidth()
                    .buttonColor(UiColor.LIME)
                    .onClick(event -> {
                        var center = signCenter();
                        var symbol = SignRegistries.SYMBOLS.get(RoadSign.DEFAULT_SYMBOL_TEXTURE);
                        var element = new SymbolElement(
                            center.x(), center.y(), Alignment.CENTER,
                            symbol
                        );
                        sign.addElement(element);
                    }),
                button(t("+", "clicksigns.editor.elements.add_text"))
                    .growWidth()
                    .buttonColor(UiColor.LIME)
                    .onClick(event -> {
                        var center = signCenter();
                        var element = new TextElement(
                            center.x(), center.y(), Alignment.TEXT_CENTER,
                            "", 1.0f, TextStyle.DEFAULT
                        );
                        sign.addElement(element);
                    }),
                button(t("+", "clicksigns.editor.elements.add_plate"))
                    .growWidth()
                    .buttonColor(UiColor.LIME)
                    .onClick(event -> {
                        var center = signCenter();
                        var element = new PlateElement(
                            center.x(), center.y(), Alignment.CENTER,
                            sign.frontSource().resize(8, 6),
                            sign.backSource().resize(8, 6)
                        );
                        sign.addElement(element);
                    }),
                // Add tools
                smallHeader(t("clicksigns.editor.tools")),
                button(t("⏪", "clicksigns.editor.tools.reset_texts"))
                    .growWidth()
                    .buttonColor(UiColor.ORANGE)
                    .onClick(event -> {
                        var elements = new ArrayList<>(sign.elements());
                        for (var element : elements) {
                            if (element.current() instanceof TextElement) {
                                sign.updateElement(element.id(),
                                    edited -> ((TextElement) edited).withText(""));
                            }
                        }
                        signViewRef.get().resetTextFieldCache();
                    }),
                button(t("🗑", "clicksigns.editor.tools.remove_elements"))
                    .growWidth()
                    .buttonColor(UiColor.MAROON)
                    .onClick(event -> {
                        var elements = new ArrayList<>(sign.elements());
                        for (var element : elements) {
                            sign.removeElement(element.id());
                        }
                    }),
                smallHeader(t("clicksigns.editor.export")),
                button(t("📤", "clicksigns.editor.export_template"))
                    .growWidth()
                    .buttonColor(UiColor.TEAL)
                    .onClick(event -> {
                        new TemplateExportScreen(sign).open();
                    })
            );
        }
    }

    /**
     * The element controls, meant for editing the selected element.
     */
    private class ElementControls extends UiComponent<ElementControls> {
        @Override
        protected void build() {
            childGap(4);
            add(fancyHeader(t("clicksigns.editor.element_properties")));

            if (selected == null) {
                // No element selecteed
                add(box().height(8)); // Spacer
                add(text(t("clicksigns.editor.no_element_selected"))
                    .alignTextCenter()
                    .style(style()
                        .alpha(0.6f)));
                add(box().height(8)); // Spacer
                add(text(t("clicksigns.editor.click_to_select"))
                    .alignTextCenter()
                    .style(style()
                        .alpha(0.6f)));
                return;
            }

            // Text controls
            // TODO: Fix in ClickUI, text fields scissor breaks when scrolling
            var current = selected.current();
            if (current instanceof TextElement text) {
                add(smallHeader(l("Text Color")));
                var colorResolver = sign.colorResolver();
                // Foreground color
                var style = text.style();
                add(colorField(
                    selected.id() + "-fg",
                    null,
                    style.color(),
                    colorResolver,
                    newColor -> {
                        if (selected == null) return;
                        sign.updateElement(selected.id(),
                            element -> ((TextElement) element)
                                .withStyle(s ->
                                    s.withColor(newColor)));
                    }
                ));

                // Background color
                add(smallHeader(l("Background Color")).padding(0));
                add(colorField(
                    selected.id() + "-bg",
                    null,
                    style.backgroundColor().orElse(""),
                    colorResolver,
                    newColor -> {
                        if (selected == null) return;
                        var newColorValue = newColor.isEmpty()
                            ? null
                            : newColor;
                        sign.updateElement(selected.id(),
                            element -> ((TextElement) element)
                                .withStyle(s ->
                                    s.withBackgroundColor(newColorValue)));
                    }
                ));


                add(smallHeader(l("Outline Color")));
                // Outline color
                add(colorField(
                    selected.id() + "-outline",
                    null,
                    style.outlineColor().orElse(""),
                    colorResolver,
                    newColor -> {
                        if (selected == null) return;
                        var newColorValue = newColor.isEmpty()
                            ? null
                            : newColor;
                        sign.updateElement(selected.id(),
                            element -> ((TextElement) element)
                                .withStyle(s ->
                                    s.withOutlineColor(newColorValue)));
                    }
                ));

                // Outline Width
                if (style.outlineColor().isPresent()) {
                    add(smallHeader(l("Outline Width")).padding(0));
                    add(memo(selected.id() + "-outline-width", () -> new NumberControl()
                        .value(style.outlineWidth())
                        .unit(l("pt"))
                        .minValue(1) // Don't allow 0
                        .maxValue(MAX_OUTLINE_WIDTH)
                        .onValueChanged(newWidth -> {
                            if (selected == null) return;
                            sign.updateElement(selected.id(),
                                element -> ((TextElement) element)
                                    .withStyle(s -> s.withOutlineWidth(newWidth)));
                        })
                    ));
                }

                // Padding
                if (style.isPaddingShown()) {
                    add(smallHeader(l("Text Padding")));
                    add(memo(selected.id() + "-padding", () -> new SizeControls(new Size(style.paddingX(), style.paddingY()))
                        .minSize(new Size(0, 0))
                        .maxSize(new Size(MAX_PADDING, MAX_PADDING))
                        .widthHeader(l("Horizontal"))
                        .heightHeader(l("Vertical"))
                        .unit(t("pt"))
                        .changeAmount(1)
                        .fineChangeAmount(0)
                        .onSizeChanged(newPadding -> {
                            if (selected == null) return;
                            sign.updateElement(selected.id(),
                                element -> ((TextElement) element).withStyle(s -> s
                                    .withPaddingX(newPadding.width())
                                    .withPaddingY(newPadding.height())));
                        })));
                }

                // Scale
                add(smallHeader(l("Font Size")));
                add(memo(selected.id() + "-font-size", () -> new NumberControl()
                    .unit(l("pt"))
                    .changeAmount(1)
                    .fastChangeAmount(4)
                    .minValue(MIN_TEXT_PT)
                    .maxValue(MAX_TEXT_PT)
                    .value((int) (text.scale() * 9f)) // Convert scale to pt
                    .onValueChanged(newPt -> {
                        if (selected == null) return;
                        var newScale = ((float) newPt) / 9f; // Convert pt to scale
                        sign.updateElement(selected.id(),
                            element -> ((TextElement) element)
                                .withScale(newScale));
                    })
                ));

                if (text.lines().size() > 1) {
                    // Line Controls
                    add(smallHeader(l("Line Alignment")).padding(0));
                    add(memo(selected.id() + "-line-alignment", () -> new AlignmentSelector()
                        .alignment(switch (text.style().textAlignment()) {
                            case LEFT -> Alignment.TEXT_LEFT;
                            case CENTER -> Alignment.TEXT_CENTER;
                            case RIGHT -> Alignment.TEXT_RIGHT;
                        })
                        .textOnly(true)
                        .onAlignmentChange(newAlignment -> {
                            if (selected == null) return;
                            sign.updateElement(selected.id(),
                                element -> ((TextElement) element)
                                    .withStyle(s -> s
                                        .withTextAlignment(switch (newAlignment) {
                                            case TOP_LEFT -> TextStyle.TextAlignment.LEFT;
                                            case TOP_CENTER -> TextStyle.TextAlignment.CENTER;
                                            case TOP_RIGHT -> TextStyle.TextAlignment.RIGHT;
                                            default -> s.textAlignment();
                                        })));
                        })
                    ));

                    add(smallHeader(l("Line Gap")).padding(0));
                    add(memo(selected.id() + "-line-gap", () -> new NumberControl()
                        .unit(l("pt"))
                        .changeAmount(1)
                        .fastChangeAmount(4)
                        .minValue(0)
                        .maxValue(20)
                        .value(text.style().lineGap())
                        .onValueChanged(newGap -> {
                            if (selected == null) return;
                            sign.updateElement(selected.id(),
                                element -> ((TextElement) element)
                                    .withStyle(s -> s.withLineGap(newGap)));
                        })
                    ));
                }
            }

            // Plate controls
            if (current instanceof PlateElement plate) {
                // TODO: Make plates by default match the sign's textures. But allow decoupling them.
                add(smallHeader(l("Plate Textures")));
                add(box()
                    .horizontal()
                    .growWidth()
                    .childGap(4)
                    .children(
                        box()
                            .growWidth()
                            .childGap(4)
                            .children(
                                smallHeader(l("Front")).padding(0),
                                new TextureButton(plate.frontSource(), newTexture -> {
                                    if (selected == null) return;
                                    sign.updateElement(selected.id(),
                                        element -> ((PlateElement) element)
                                            .withFrontSource(newTexture.resizeToFit(((PlateElement) element).size())));
                                })
                            ),
                        box()
                            .growWidth()
                            .childGap(4)
                            .children(
                                smallHeader(l("Back")).padding(0),
                                new TextureButton(plate.backSource(), newTexture -> {
                                    if (selected == null) return;
                                    sign.updateElement(selected.id(),
                                        element -> ((PlateElement) element)
                                            .withBackSource(newTexture.resizeToFit(((PlateElement) element).size())));
                                })
                            )
                    )
                );
                add(smallHeader(l("Plate Size")));
                add(memo(selected.id() + "-plate-size", () -> new SizeControls(plate.size())
                    .minSize(MIN_PLATE_SIZE)
                    .maxSize(MAX_PLATE_SIZE)
                    .changeAmount(8)
                    .fineChangeAmount(1)
                    .onSizeChanged(newSize -> {
                        if (selected == null) return;
                        sign.updateElement(selected.id(),
                            element -> {
                                var plateElement = (PlateElement) element;
                                var newFront = plateElement.frontSource().resizeToFit(newSize);
                                var newBack = plateElement.backSource().resizeToFit(newSize);
                                return plateElement.withFrontSource(newFront).withBackSource(newBack);
                            });
                    })
                ));
            }

            // Symbol controls
            if (current instanceof SymbolElement symbol) {
                add(smallHeader(l("Symbol")));
                // TODO: Symbol selection button
                // TODO: Color replacement? Even better, make texture edit screen
                add(new SymbolView(symbol, sign.colorResolver())
                    .padding(4)
                    .style(style()
                        .borderColor(UiColor.GRAY)
                        .backgroundColor(UiUtil.primaryColorOf(sign.frontSource().resolve(sign.colorResolver())))
                        .whenHovered(style()
                            .borderColor(UiColor.RED)))
                    .tooltip(descriptions(
                        describeLeftClick(t("clicksigns.overview.symbol.tooltip.leftClick")),
                        describeRightClick(t("clicksigns.overview.symbol.tooltip.rightClick"))
                    ))
                    .onClick(event -> {
                        event.playSound();
                        if (selected == null) return;
                        SymbolView.handleSymbolChange(sign, selected, event);
                    }));
            }

            // Add Alignment
            add(smallHeader(l("Element Alignment")));
            add(memo(selected.id() + "-alignment", () -> new AlignmentSelector()
                .alignment(current.alignment())
                // TODO: Decide if good to limit to text alignment only
                .onAlignmentChange(newAlignment -> {
                    if (selected == null) return;
                    sign.updateElement(selected.id(),
                        element -> element.withAlignment(newAlignment));
                })));

            // Delete button
            add(smallHeader(t("clicksigns.editor.other")));
            add(button(t("🗑", "clicksigns.editor.tools.remove_element"))
                .growWidth()
                .buttonColor(UiColor.MAROON)
                .onClick(event -> {
                    if (selected == null) return;
                    sign.removeElement(selected.id());
                }));
        }
    }

    private Point signCenter() {
        return new Point(sign.width() / 2, sign.height() / 2);
    }

    private void spawnElementNearSelected(@Nullable EditableSignElement element) {
        if (element == null) return;
        Point position;
        if (selected == null) {
            position = signCenter();
        } else {
            var currentSelected = selected.current();
            position = new Point(
                // Position the new element offset from the selected element, so they don't overlap
                (int) (currentSelected.x() + currentSelected.width() / 2),
                (int) (currentSelected.y() + currentSelected.height() / 2)
            );
        }
        var newElement = element.current().withPosition(position.x(), position.y());
        var editable = sign.addElement(newElement);
        selected(editable);
    }


    /**
     * Creates a color input box with validation and suggestions.
     *
     * @param id             The unique identifier for the UI element.
     * @param tooltip        The tooltip text to display on hover.
     * @param value          The initial color value as a string.
     * @param colorResolver  The ColorResolver to validate and suggest colors.
     * @param onValueChanged A consumer that handles changes to the color value.
     * @return A configured UiElement representing the color input box.
     */
    private UiElement<?> colorField(
        String id,
        String tooltip,
        String value,
        ColorResolver colorResolver,
        Consumer<String> onValueChanged
    ) {
        var color = UiColor.of(colorResolver.resolveOrDefault(value, Color.WHITE));
        return memo(id, () ->
            textField()
                .growWidth()
                .highlightInvalid(true)
                .tooltip(tooltip)
                .textShadow(false)
                .value(value)
                .onValueChanged(onValueChanged))
            .suggest(colorResolver::suggestColor)
            .validator(string -> {
                if (string.isEmpty()) return true;
                return colorResolver.isValidColor(string);
            })
            .style(style()
                .textColor(color)
                .backgroundColor(
                    color.pickBetterContrasting(UiColor.BLACK, UiColor.WHITE)
                ));
    }
}
