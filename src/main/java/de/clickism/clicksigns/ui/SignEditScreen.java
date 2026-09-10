package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.editor.EditableSignElement;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.ui.elements.SymbolView;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.elements.Box;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.layout.Point;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import static de.clickism.clicksigns.ui.UiConstants.TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

// TODO: Split into different classes
public class SignEditScreen extends UiScreen<SignEditScreen>
    implements FancyHeaders {

    private static final Size MIN_SIGN_SIZE = new Size(6, 6);
    private static final Size MAX_SIGN_SIZE = new Size(144, 144); // 9 Blocks

    private static final Size MIN_PLATE_SIZE = new Size(4, 4);
    private static final Size MAX_PLATE_SIZE = MAX_SIGN_SIZE;

    private static final float MIN_TEXT_SCALE = 0.3f;
    private static final float MAX_TEXT_SCALE = 6.0f;

    private final EditableRoadSign sign;
    private final Ref<SignView> signViewRef = ref();
    private final Ref<SignControls> signControlsRef = ref();
    private final Ref<SignEditor> signEditorRef = ref();
    private final Ref<ElementControls> elementControlsRef = ref();
    private @Nullable EditableSignElement selected = null;
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

        @Override
        protected void build() {
            childGap(8);
            children(box().childGap(8).growHeight().alignCenter().children(
                // TODO: Decide if we want the sign view to be centered or partially.
                // Sign view
                memo(() -> new SignView(sign)
                    .ref(signViewRef)
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
                                            var localOrigin = new Point(signElement.localX(), signElement.localY());
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
                                dragStartX = editable.current().localX();
                                dragStartY = editable.current().localY();
                                dragged = editable;
                                signViewRef.get().renderGuidelines(true);
                            })
                            // Drag controls
                            .onDrag(event -> {
                                // Get the delta in sign space
                                int deltaX = (int) (event.totalDeltaX() / TEXTURE_RENDER_SCALE);
                                int deltaY = (int) (event.totalDeltaY() / TEXTURE_RENDER_SCALE);

                                int newX = dragStartX + deltaX;
                                int newY = dragStartY - deltaY;

                                if (dragged == null) return;

                                var currentElement = dragged.current();
                                if (newX == currentElement.localX() && newY == currentElement.localY()) {
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
                    })),
                box()
                    // Use panel height from overview screen to keep alignment consistent
                    .height(SignOverviewScreen.PANEL_HEIGHT)
                    .children(
                        box()
                            .alignCenter()
                            .padding(4)
                            .childGap(8)
                            // Make width equivalent to 2 block signs
                            .width(32 * TEXTURE_RENDER_SCALE)
                            .style(style()
                                .backgroundColor(UiColor.BLACK_A50))
                            .children(
                                // Sign controls
                                new SizeControls(new Size(sign.width(), sign.height()))
                                    .minSize(MIN_SIGN_SIZE)
                                    .maxSize(MAX_SIGN_SIZE)
                                    .onSizeChanged(newSize -> {
                                        sign.resize(newSize.width(), newSize.height());
                                    }),
                                // Confirm button
                                button(ComponentUtil.confirmWithIcon())
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
                    .childGap(8)
                    .children(
                        box()
                            .growWidth()
                            .childGap(8)
                            .children(
                                smallHeader(l("Front")),
                                new TextureButton(sign.frontSource(), newTexture -> {
                                    sign.frontSource(newTexture.resizeToFit(sign.build()));
                                })
                            ),
                        box()
                            .growWidth()
                            .childGap(8)
                            .children(
                                smallHeader(l("Back")),
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
                            center.x(), center.y(), Alignment.TEXT_RIGHT,
                            "", 1.0f, "foreground", null
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

        private Point signCenter() {
            return new Point(sign.width() / 2, sign.height() / 2);
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
            var current = selected.current();
            if (current instanceof TextElement text) {
                add(smallHeader(l("Color")));

                var colorResolver = sign.colorResolver();
                // Foreground color
                var foregroundColor = UiColor.of(colorResolver.resolveOrDefault(text.color(), Color.WHITE));
                add(
                    memo(selected.id() + "-fg", () -> textField()
                        .growWidth()
                        .highlightInvalid(true)
                        .tooltip("Text Color")
                        .textShadow(false)
                        .value(text.color())
                        .onValueChanged(newColor -> {
                            if (selected == null) return;
                            sign.updateElement(selected.id(),
                                element -> ((TextElement) element).withColor(newColor));
                        })
                    )
                        // Apply these after memo, so they are refreshed every rebuild
                        .suggest(colorResolver::suggestColor)
                        .validator(colorResolver::isValidColor)
                        .style(style()
                            .textColor(foregroundColor)
                            .backgroundColor(foregroundColor.pickBetterContrasting(UiColor.BLACK, UiColor.WHITE)))
                );

                // Background color
                // TODO: Refactor into colorTextField
                var backgroundColor = UiColor.of(colorResolver.resolveOrDefault(text.backgroundColor(), Color.WHITE));
                add(
                    memo(selected.id() + "-bg", () -> textField()
                        .growWidth()
                        .highlightInvalid(true)
                        .tooltip("Background Color")
                        .textShadow(false)
                        .value(text.backgroundColor() == null
                            ? ""
                            : text.backgroundColor())
                        .onValueChanged(newColor -> {
                            if (selected == null) return;
                            var newColorValue = newColor.isEmpty()
                                ? null
                                : newColor;
                            sign.updateElement(selected.id(),
                                element -> ((TextElement) element).withBackgroundColor(newColorValue));
                        })
                    )
                        // Apply these after memo, so they are refreshed every rebuild
                        .suggest(colorResolver::suggestColor)
                        .validator(color -> {
                            if (color == null || color.isEmpty()) return true;
                            return colorResolver.isValidColor(color);
                        })
                        .style(style()
                            .textColor(backgroundColor)
                            .backgroundColor(backgroundColor.pickBetterContrasting(UiColor.BLACK, UiColor.WHITE)))
                );

                // Scale
                add(smallHeader(l("Scale")));
                add(memo(selected.id() + "-scale", () -> numberField()
                    .growWidth()
                    .allowDecimal(true)
                    .value(text.scale())
                    .onNumberChanged(newScale -> {
                        if (selected == null) return;
                        var clamped = Mth.clamp(newScale.floatValue(), MIN_TEXT_SCALE, MAX_TEXT_SCALE);
                        sign.updateElement(selected.id(),
                            // TODO: Add buttons to increase/decrease scale by 0.1
                            element -> ((TextElement) element).withScale(clamped));
                        signViewRef.get().resetTextFieldCache();
                    })
                ));
            }

            // Plate controls
            if (current instanceof PlateElement plate) {
                add(smallHeader(l("Plate Textures")));
                add(box()
                    .horizontal()
                    .growWidth()
                    .childGap(8)
                    .children(
                        box()
                            .growWidth()
                            .childGap(8)
                            .children(
                                smallHeader(l("Front")),
                                new TextureButton(plate.front(), newTexture -> {
                                    if (selected == null) return;
                                    sign.updateElement(selected.id(),
                                        element -> ((PlateElement) element).withFront(newTexture.resizeToFit(element.signSize())));
                                })
                            ),
                        box()
                            .growWidth()
                            .childGap(8)
                            .children(
                                smallHeader(l("Back")),
                                new TextureButton(plate.back(), newTexture -> {
                                    if (selected == null) return;
                                    sign.updateElement(selected.id(),
                                        element -> ((PlateElement) element).withBack(newTexture.resizeToFit(element.signSize())));
                                })
                            )
                    )
                );
                add(smallHeader(l("Plate Size")));
                add(memo(selected.id() + "-plate-size", () -> new SizeControls(plate.signSize())
                    .minSize(MIN_PLATE_SIZE)
                    .maxSize(MAX_PLATE_SIZE)
                    .onSizeChanged(newSize -> {
                        if (selected == null) return;
                        sign.updateElement(selected.id(),
                            element -> {
                                var plateElement = (PlateElement) element;
                                var newFront = plateElement.front().resizeToFit(newSize);
                                var newBack = plateElement.back().resizeToFit(newSize);
                                return plateElement.withFront(newFront).withBack(newBack);
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
                    .tooltip(t("clicksigns.overview.symbol.tooltip"))
                    .onClick(event -> {
                        event.playSound();
                        if (selected == null) return;
                        SymbolView.handleSymbolChange(sign, selected, event);
                    }));
            }

            // Add Alignment
            add(smallHeader(l("Alignment")));
            add(memo(selected.id() + "-alignment", () -> new AlignmentSelector()
                .alignment(current.alignment())
                .textOnly(current instanceof TextElement)
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
}
