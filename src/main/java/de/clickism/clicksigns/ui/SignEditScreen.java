package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.*;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.reactivity.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.t;

public class SignEditScreen extends UiScreen<SignEditScreen> {
    private @Nullable SignElement selected = null;
    private Consumer<RoadSign> onSignUpdate = sign -> {};

    private final Ref<SignView> signViewRef = ref();
    private final Ref<SignControls> signControlsRef = ref();
    private final Ref<SignEditor> signEditorRef = ref();
    private final Ref<ElementControls> elementControlsRef = ref();

    private RoadSign sign;

    public SignEditScreen(@NotNull RoadSign sign) {
        this.sign = sign;
    }

    public SignEditScreen onSignUpdate(Consumer<RoadSign> onSignUpdate) {
        this.onSignUpdate = onSignUpdate;
        return this;
    }

    private void updateSign(Function<RoadSign, RoadSign> updater) {
        this.sign = updater.apply(currentSign());
        this.signViewRef.get().roadSign(this.sign);
    }

    private RoadSign currentSign() {
        return this.sign
            // Read the elements from the sign view
            .withElements(this.signViewRef.get().readElements());
    }

    private void selected(SignElement element) {
        this.selected = element;
        this.elementControlsRef.get().element.update(element);
    }

    @Override
    public void build() {
        this.grow()
            .horizontal()
            .children(
                // Left panel
                panel()
                    .children(
                        // Scroll container
                        box()
                            .grow()
                            .scrollable(true)
                            .children(
                                new SignControls()
                                    .ref(signControlsRef)
                                    .grow()
                                    .crossAlign(Align.CENTER)
                            )
                    ),

                // Center editor
                // TODO: Make scrollable?
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
                        // Scroll container
                        box()
                            .grow()
                            .scrollable(true)
                            .children(
                                new ElementControls()
                                    .ref(elementControlsRef)
                                    .grow()
                                    .crossAlign(Align.CENTER)
                            )
                    )
            );
    }

    private class SignEditor extends UiComponent<SignEditor> {

        private int dragStartX = 0;
        private int dragStartY = 0;
        private SignElement draggedElement = null;
        private int draggedElementIndex = -1;

        @Override
        protected void build() {
            childGap(8);
            children(
                // TODO: Resize controls!
                // Sign view
                new SignView()
                    .ref(signViewRef)
                    .roadSign(sign)
                    .elementConfig((uiElement, signElement) -> {
                        uiElement
                            // Hover style
                            .style(s -> s
                                .whenHovered(h -> h
                                    .border(UiColor.RED))
                                .when(context -> signElement == selected || signElement == draggedElement, l -> l
                                    // TODO: Render origin
                                    .border(UiColor.GREEN)))
                            // Update selected on click
                            .onClick(event -> {
                                selected(signElement);
                            })
                            .onDragStart(event -> {
                                dragStartX = signElement.localX();
                                dragStartY = signElement.localY();
                                draggedElement = signElement;
                                draggedElementIndex = signViewRef.get().readElements().indexOf(signElement);
                            })
                            // Drag controls
                            .onDrag(event -> {
                                // Get the delta in sign space
                                int deltaX = (int) (event.totalDeltaX() / DEFAULT_TEXTURE_RENDER_SCALE);
                                int deltaY = (int) (event.totalDeltaY() / DEFAULT_TEXTURE_RENDER_SCALE);

                                int newX = dragStartX + deltaX;
                                int newY = dragStartY - deltaY;

                                if (draggedElement == null) return;
                                if (draggedElementIndex == -1) return;

                                if (newX == draggedElement.localX() && newY == draggedElement.localY()) {
                                    // No change
                                    return;
                                }

                                // Replace the element in the sign with a new one at the new position
                                updateSign(sign -> {
                                    // Use index since it is stable, and the element may have been replaced already
                                    var element = sign.elements().get(draggedElementIndex);
                                    var newElement = element.withPosition(newX, newY);
                                    draggedElement = newElement;
                                    return sign.replaceElement(element, newElement);
                                });
                            });
                    }),
                box().height(16), // Spacer
                // Show size
                text("Size: %d x %d".formatted(sign.width(), sign.height())),
                // Confirm
                button(ComponentUtil.confirmWithIcon())
                    .onClick(event -> {
                        // Callback and close
                        onSignUpdate.accept(currentSign());
                        close();
                    })
            );
        }
    }

    /**
     * The sign controls, for editing general info about ths sign,
     * such as textures or adding elements.
     */
    private class SignControls extends UiComponent<SignControls> {
        @Override
        protected void build() {
            children(
                h4(t("clicksigns.editor.sign_properties")),
                // Add texture selection
                h5(t("clicksigns.editor.sign_textures")),

                // Add element controls
                h5(t("clicksigns.editor.elements")),
                // Add tools
                h5(t("clicksigns.editor.tools"))
            );
        }
    }

    /**
     * The element controls, meant for editing the selected element.
     */
    private class ElementControls extends UiComponent<ElementControls> {
        private final State<SignElement> element = state(null);

        @Override
        protected void build() {
            var element = this.element.get();

            add(h4(t("clicksigns.editor.element_properties")));

            if (element == null) {
                // No element selecteed
                add(text(t("clicksigns.editor.no_element_selected"))
                    .style(s -> s
                        .alpha(0.5f)));
                return;
            }

            // Text controls
            if (element instanceof TextElement text) {
                add(h5(t("clicksigns.editor.text_element")));
                add(textField(text.text())
                    .value(text.text())
                    .onValueChanged(newText -> {
                        updateSign(sign -> sign.replaceElement(selected, text.withText(newText)));
                    }));

                add(h5("Color"));
                add(textField()
                    .value(text.color())
                    .onValueChanged(newColor -> {
                        updateSign(sign -> sign.replaceElement(selected, text.withColor(newColor)));
                    })
                );
            }

            // Delete button
            add(h5(t("clicksigns.editor.other")));
            add(button(t("🗑", "clicksigns.editor.tools.remove_element"))
                .onClick(event -> {
                    updateSign(sign -> sign.removeElement(selected));
                }));
        }
    }

    private UiElement<?> panel() {
        var panelWidth = 120;
        return box()
            .width(panelWidth)
            .growHeight()
            .padding(8)
            .style(s -> s
                .background(UiColor.BLACK_A70)
                .border(UiColor.LIGHT_GRAY).alpha(0.5f));
    }
}
