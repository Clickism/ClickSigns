package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.components.SizeControls;
import de.clickism.clicksigns.ui.components.sign.SignView;
import de.clickism.clicksigns.ui.screen.SignOverviewScreen;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.event.events.MouseClickEvent;
import de.clickism.clickui.layout.Point;

import java.util.function.Consumer;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;

class SignEditorView extends UiComponent<SignEditorView> {
    private final Ref<SignView> signViewRef = ref();
    private final SignEditorContext context;
    private Consumer<MouseClickEvent> onConfirm = event -> {};

    private final EditorActionHandler actionHandler;

    public SignEditorView(SignEditorContext context, EditorActionHandler actionHandler) {
        this.context = context;
        this.actionHandler = actionHandler;
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
                                    .when(sc -> context.isSelected(editable),
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
                                    actionHandler.handleMouseDown(editable);
                                    signViewRef.get().invalidateTree();
                                })
                                .onRelease(event -> {
                                    actionHandler.handleMouseUp(editable);
                                })
                                .onDragStart(event -> {
                                    actionHandler.handleDragStart(event);
                                    signViewRef.get().renderGuidelines(true);
                                })
                                // Drag controls
                                .onDrag(actionHandler::handleDrag)
                                .onDragEnd(event -> {
                                    actionHandler.handleDragEnd();
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


}
