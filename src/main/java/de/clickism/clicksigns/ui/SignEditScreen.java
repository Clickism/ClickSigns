package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.editor.EditableSignElement;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.*;
import de.clickism.clickui.elements.Box;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.layout.Point;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Consumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

public class SignEditScreen extends UiScreen<SignEditScreen> {

    private EditableRoadSign sign;
    private @Nullable EditableSignElement selected = null;

    private Consumer<RoadSign> onSignUpdate = sign -> {};

    private final Ref<SignView> signViewRef = ref();
    private final Ref<SignControls> signControlsRef = ref();
    private final Ref<SignEditor> signEditorRef = ref();
    private final Ref<ElementControls> elementControlsRef = ref();

    public SignEditScreen(@NotNull RoadSign sign) {
        this.sign = new EditableRoadSign(sign);
        this.sign.addChangeListener(change -> {
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

    private class SignEditor extends UiComponent<SignEditor> {

        private int dragStartX = 0;
        private int dragStartY = 0;
        private EditableSignElement dragged = null;

        @Override
        protected void build() {
            childGap(8);
            var build = sign.build();
            children(box().childGap(16).growHeight().children(
                // TODO: Resize controls!
                box().growHeight(), // To center the sign view vertically
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
                                            GuiUtils.renderPlusOnTop(
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
                                int deltaX = (int) (event.totalDeltaX() / DEFAULT_TEXTURE_RENDER_SCALE);
                                int deltaY = (int) (event.totalDeltaY() / DEFAULT_TEXTURE_RENDER_SCALE);

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
                    .growWidth()
                    .growHeight()
                    .crossAlign(Align.CENTER)
                    .children(
                        box()
                            .alignCenter()
                            .padding(4)
                            .childGap(8)
                            .growWidth()
                            // Make max width equivalent to 2 block signs
                            .maxWidth(32 * DEFAULT_TEXTURE_RENDER_SCALE)
                            .style(style()
                                .backgroundColor(UiColor.BLACK_A50))
                            .children(
                                // Show size
                                smallHeader(l("Size"))
                                    .padding(0),
                                text("%d x %d".formatted(build.width(), build.height())),
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
                                textureButton(sign.frontSource(), newTexture -> {
                                    sign.frontSource(newTexture.resizeToFit(sign.build()));
                                })
                            ),
                        box()
                            .growWidth()
                            .childGap(8)
                            .children(
                                smallHeader(l("Back")),
                                textureButton(sign.backSource(), newTexture -> {
                                    sign.backSource(newTexture.resizeToFit(sign.build()));
                                })
                            )
                    ),
                // Add element controls
                smallHeader(t("clicksigns.editor.elements")),

                button(t("+", "clicksigns.editor.elements.add_symbol"))
                    .growWidth()
                    .buttonColor(UiColor.GREEN)
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
                    .buttonColor(UiColor.GREEN)
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
                    .buttonColor(UiColor.GREEN)
                    .onClick(event -> {
                        var center = signCenter();
                        var built = sign.build();
                        var element = new PlateElement(
                            center.x(), center.y(), Alignment.CENTER,
                            built.frontSource().resize(8, 6),
                            built.backSource().resize(8, 6)
                        );
                        sign.addElement(element);
                    }),
                // Add tools
                smallHeader(t("clicksigns.editor.tools")),
                button(t("⏪", "clicksigns.editor.tools.reset_texts"))
                    .growWidth()
                    .buttonColor(UiColor.BLUE)
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
                    .buttonColor(UiColor.RED)
                    .onClick(event -> {
                        var elements = new ArrayList<>(sign.elements());
                        for (var element : elements) {
                            sign.removeElement(element.id());
                        }
                    }),
                smallHeader(t("clicksigns.editor.export")),
                button(t("📤", "clicksigns.editor.export_template"))
                    .growWidth()
                    .buttonColor(UiColor.CYAN)
                    .onClick(event -> {
                        // open the export screen here
                    })
            );
        }

        private Point signCenter() {
            var build = sign.build();
            return new Point(build.width() / 2, build.height() / 2);
        }

        private UiElement<?> textureButton(TextureSource source, Consumer<TextureSource> onTextureSelected) {
            var texture = source.resize(16, 16).resolve(ColorResolver.empty());
            return image(texture.location(), 40, 40)
                .keepAspectRatio(true)
                .grow()
                .style(style()
                    .whenHovered(style()
                        .borderColor(UiColor.RED)))
                .onClick(event -> {
                    event.playSound();
                    if (GuiUtils.isLeftClick(event.button())) {
                        // Cycle to next texture in the same category
                        if (source instanceof TiledTextureSource tiled) {
                            var tileSet = tiled.resolveTileSet();
                            if (tileSet == null) return;
                            var nextTileSet = tileSet.nextInCategory();
                            var nextTexture = TiledTextureSource.unsized(nextTileSet.identifier());
                            onTextureSelected.accept(nextTexture);
                        }
                    } else {
                        // Open texture menu
                        // TODO: Handle non-tile-set textures (e.g. custom textures)
                        var entries = SignRegistries.TILE_SETS.all().stream()
                            .map(tileSet -> new TextureList.Entry(
                                new TiledTextureSource(tileSet.identifier(), 16, 16)
                                    .resolve(tileSet.colorResolver()),
                                tileSet.identifier(),
                                // TODO: Handle uncategorized symbols
                                tileSet.resolveCategory()
                            ))
                            .toList();

                        new TextureSelectScreen(l("Select Texture"), entries)
                            .onTextureSelected(entry -> {
                                onTextureSelected.accept(TiledTextureSource.unsized(entry.identifier()));
                            }).open();
                    }
                });
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

                var colorResolver = sign.build().colorResolver();
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
                        .validator(colorResolver::isValidColor)
                        .style(style()
                            .textColor(foregroundColor)
                            .backgroundColor(foregroundColor.pickBetterContrasting(UiColor.BLACK, UiColor.WHITE)))
                );

                // Background color
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
                        sign.updateElement(selected.id(),
                            element -> ((TextElement) element).withScale(newScale.floatValue()));
                    })));
            }

            // Add Alignment
            add(smallHeader(l("Alignment")));
            add(memo(selected.id() + "-alignment", () -> new AlignmentSelector()
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
                .buttonColor(UiColor.RED)
                .onClick(event -> {
                    if (selected == null) return;
                    sign.removeElement(selected.id());
                }));
        }
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

    private Box fancyHeader(Component text) {
        return box()
            .padding(4)
            .growWidth()
            .alignCenter()
            .style(style()
                .borderColor(UiColor.WHITE_A30)
                .backgroundColor(UiColor.WHITE_A10))
            .children(
                text(text)
            );
    }

    private Box smallHeader(Component text) {
        return box()
            .growWidth()
            .padding(8, 0, 0, 0)
            .children(
                box()
                    .padding(3, 0, 2, 0)
                    .growWidth()
                    .alignCenter()
                    .style(style()
                        .borderColorBottom(UiColor.WHITE_A30)
                        .backgroundColor(UiColor.WHITE.alpha(0.05f)))
                    .children(
                        text(text)
                            .style(style()
                                .fontScale(0.75f)
                                .alpha(0.8f))
                    ));
    }
}
