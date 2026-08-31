package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.editor.EditableSignElement;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clickui.*;
import de.clickism.clickui.layout.Align;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
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
        private EditableSignElement draggedElement = null;

        @Override
        protected void build() {
            childGap(8);
            var build = sign.build();
            children(
                // TODO: Resize controls!
                // Sign view
                memo(() -> new SignView(sign)
                    .ref(signViewRef)
                    .elementConfig((uiElement, editableElement) -> {
                        uiElement
                            // Hover style
                            .style(s -> s
                                .whenHovered(h -> h
                                    .border(UiColor.RED))
                                .when(context -> editableElement.equals(selected) ||
                                                 editableElement.equals(draggedElement), l -> l
                                    // TODO: Render origin
                                    .border(UiColor.GREEN)))
                            // Update selected on click
                            .onClick(event -> {
                                selected(editableElement);
                            })
                            .onDragStart(event -> {
                                dragStartX = editableElement.current().localX();
                                dragStartY = editableElement.current().localY();
                                draggedElement = editableElement;
                            })
                            // Drag controls
                            .onDrag(event -> {
                                // Get the delta in sign space
                                int deltaX = (int) (event.totalDeltaX() / DEFAULT_TEXTURE_RENDER_SCALE);
                                int deltaY = (int) (event.totalDeltaY() / DEFAULT_TEXTURE_RENDER_SCALE);

                                int newX = dragStartX + deltaX;
                                int newY = dragStartY - deltaY;

                                if (draggedElement == null) return;

                                var currentElement = draggedElement.current();
                                if (newX == currentElement.localX() && newY == currentElement.localY()) {
                                    // No change
                                    return;
                                }

                                // Replace the element in the sign with a new one at the new position
                                sign.updateElement(
                                    draggedElement.id(),
                                    element -> element.withPosition(newX, newY)
                                );
                            });
                    })),
                box().height(16), // Spacer
                // Show size
                text("Size: %d x %d".formatted(build.width(), build.height())),
                // Confirm button
                button(ComponentUtil.confirmWithIcon())
                    .onClick(event -> {
                        // Callback and close
                        onSignUpdate.accept(sign.build());
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
            childGap(4);
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
        @Override
        protected void build() {
            childGap(4);
            add(h4(t("clicksigns.editor.element_properties")));

            if (selected == null) {
                // No element selecteed
                add(text(t("clicksigns.editor.no_element_selected"))
                    .style(s -> s
                        .alpha(0.5f)));
                return;
            }

            // Text controls
            var current = selected.current();
            if (current instanceof TextElement text) {
                add(h5("Color"));

                add(memo(selected.id() + "-fg", () -> textField()
                    .tooltip("Text Color")
                    .value(text.color())
                    .onValueChanged(newColor -> {
                        if (selected == null) return;
                        sign.updateElement(selected.id(),
                            element -> ((TextElement) element).withColor(newColor));
                    })
                ));

                add(memo(selected.id() + "-bg", () -> textField()
                    .tooltip("Background Color")
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
                ));
            }

            // Delete button
            add(h5(t("clicksigns.editor.other")));
            add(button(t("🗑", "clicksigns.editor.tools.remove_element"))
                .onClick(event -> {
                    if (selected == null) return;
                    sign.removeElement(selected.id());
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
