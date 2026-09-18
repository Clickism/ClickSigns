package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.editable.EditableRoadSign;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.elements.Box;
import de.clickism.clickui.layout.Align;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Screen for editing a road sign.
 */
public class SignEditorScreen extends UiScreen<SignEditorScreen>
    implements CommonComponents {
    /**
     * Main editor context
     */
    private final SignEditorContext context;
    private final EditorActionHandler actionHandler;

    private Consumer<RoadSign> onSignChange = sign -> {};

    /*
     * We keep the component and also the editor and control components stable, and handle
     *  reactivity manually.
     */
    private final Ref<SignElementControls> elementControlsRef = ref();
    private final Ref<SignPropertyControls> propertyControlsRef = ref();
    private final Ref<SignEditorView> signViewRef = ref();

    /**
     * Create a new sign edit screen for the given sign.
     *
     * @param sign The sign to edit.
     */
    public SignEditorScreen(@NotNull RoadSign sign) {
        var editableSign = new EditableRoadSign(sign);
        // Update the sign view and controls when the sign changes
        editableSign.onSignChanged(() -> {
            // Invalidate both the element controls and property controls
            this.elementControlsRef.get().invalidateTree();
            this.propertyControlsRef.get().invalidateTree();
            this.signViewRef.get().invalidateTree(); // So that size is also updated, when changed through texture editor
        });
        // Create context
        this.context = new SignEditorContext(editableSign);
        this.context.onSelectedChanged(element -> {
            // Invalidate the element controls
            this.elementControlsRef.get().invalidateTree();
        });
        // Add action handler
        this.actionHandler = new EditorActionHandler(this.context);
        this.globalEvents().onKeyPress(this.actionHandler::handleKeyPress);
    }

    /**
     * Set a callback to be called when the confirm button is clicked
     * and the sign is updated.
     *
     * @param onSignChange The callback to be called when the sign is updated.
     * @return This screen.
     */
    public SignEditorScreen onSignChange(Consumer<RoadSign> onSignChange) {
        this.onSignChange = onSignChange;
        return this;
    }

    @Override
    public void build() {
        this.grow()
            .horizontal()
            .children(
                // Left panel
                panel()
                    .children(
                        new SignPropertyControls(this.context)
                            .ref(propertyControlsRef)
                            .grow()
                            .crossAlign(Align.CENTER)
                    ),

                // Center editor
                box()
                    .grow()
                    .alignCenter()
                    .childGap(8)
                    .children(
                        new SignEditorView(this.context, this.actionHandler)
                            .ref(signViewRef)
                            .grow()
                            .alignCenter()
                            .onConfirm(event -> {
                                // Update the sign when the confirm button is clicked
                                var roadSign = this.context.roadSign().build();
                                this.onSignChange.accept(roadSign);
                                this.close();
                            })
                    ),

                // Right panel
                panel()
                    .children(
                        new SignElementControls(this.context)
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
}
