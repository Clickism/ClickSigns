package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.components.SizeControls;
import de.clickism.clicksigns.ui.components.sign.SignView;
import de.clickism.clicksigns.ui.editable.EditableSignElement;
import de.clickism.clicksigns.ui.screen.SignOverviewScreen;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.event.events.MouseClickEvent;
import de.clickism.clickui.layout.Point;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;

class SignEditorView extends UiComponent<SignEditorView> {
    private final Ref<SignView> signViewRef = ref();
    private final SignEditorContext context;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private EditableSignElement dragged = null;
    private Consumer<MouseClickEvent> onConfirm = event -> {};

    public SignEditorView(SignEditorContext context) {
        this.context = context;
        globalEvents().onKeyPress(event -> {
            // TODO: Controls
            // Duplicate selectedRef element with Ctrl+D
//            var selected = selectedRef.get();
//            if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_D) {
//                spawnElementNearSelected(selected);
//                event.consume();
//            }
//            // Copy selectedRef element with Ctrl+C
//            if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_C) {
//                if (selectedRef != null) {
//                    SignEditScreen.copied = selected;
//                    event.consume();
//                }
//            }
//            // Paste copied element with Ctrl+V
//            if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_V) {
//                signEditScreen.spawnElementNearSelected(SignEditScreen.copied);
//                event.consume();
//            }
//            // Delete selectedRef element with Delete key
//            if (event.code() == GLFW.GLFW_KEY_DELETE) {
//                // Don't use the delete key for text elements
//                if (signEditScreen.selected != null && !(signEditScreen.selected.current() instanceof TextElement)) {
//                    signEditScreen.sign.removeElement(signEditScreen.selected.id());
//                    signEditScreen.selected(null);
//                    event.consume();
//                }
//            }
        });
    }

    /**
     * Sets a callback to be invoked when the confirm button is clicked.
     *
     * @param onConfirm the callback to be invoked on confirm
     * @return this SignEditor instance for method chaining
     */
    public SignEditorView onConfirm(Consumer<MouseClickEvent> onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    @Override
    protected void build() {
        childGap(8);
        boolean canResize = context.roadSign().frontSource().isResizable();
        children(box().childGap(8).grow().alignCenter().children(
            // Sign view
            box()
                .padding(1)
                .growWidth()
                .alignCenter()
                .scrollable(true)
                .children(
                    memo(() -> new SignView(context.roadSign())
                        .ref(signViewRef)
                        .signConfig((uiElement, editable) -> {
                            uiElement.style(style()
                                .whenHovered(style()
                                    .borderColor(UiColor.RED.alpha(.3f))));
                        })
                        .elementConfig((uiElement, editable) -> {
                            uiElement
                                // Hover style
                                .style(style()
                                    .whenHovered(style()
                                        .borderColor(UiColor.RED))
                                    .when(sc -> editable.equals(context.selected()) || editable.equals(dragged),
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
                                    context.setSelected(editable);
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
                                    context.roadSign().updateElement(
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
                            new SizeControls(new Size(context.roadSign().width(), context.roadSign().height()))
                                .minSize(RoadSign.MIN_SIGN_SIZE)
                                .maxSize(RoadSign.MAX_SIGN_SIZE)
                                .changeAmount(8)
                                .fineChangeAmount(1)
                                .unit(l("px"))
                                .allowInput(canResize)
                                .onSizeChanged(newSize -> {
                                    context.roadSign().resize(newSize.width(), newSize.height());
                                }),
                            // Confirm button
                            button(ComponentUtil.confirmWithIcon())
                                .buttonColor(UiColor.LIME)
                                .growWidth()
                                .onClick(event -> {
                                    // Callback and close
                                    onConfirm.accept(event);
                                })
                        )
                )
        ));
    }

    private void spawnElementNearSelected(@Nullable EditableSignElement element) {
        if (element == null) return;
        Point position;
        var selected = context.selected();
        if (selected == null) {
            position = context.roadSign().center();
        } else {
            var currentSelected = selected.current();
            position = new Point(
                // Position the new element offset from the selected element, so they don't overlap
                (int) (currentSelected.x() + currentSelected.width() / 2),
                (int) (currentSelected.y() + currentSelected.height() / 2)
            );
        }
        var newElement = element.current().withPosition(position.x(), position.y());
        var editable = context.roadSign().addElement(newElement);
        context.setSelected(editable);
    }
}
