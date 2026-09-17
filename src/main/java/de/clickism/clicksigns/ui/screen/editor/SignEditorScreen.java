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

// TODO: Info button instead of too many tooltips
// TODO: Ability to select multiple elements and move them together/copy etc.
public class SignEditorScreen extends UiScreen<SignEditorScreen>
    implements CommonComponents {

    /**
     * Main editor context
     */
    private final SignEditorContext context;

    private Consumer<RoadSign> onSignChange = sign -> {};

    /**
     * Create a new sign edit screen for the given sign.
     *
     * @param sign The sign to edit.
     */
    public SignEditorScreen(@NotNull RoadSign sign) {
        var editableSign = new EditableRoadSign(sign);
        // Update the sign view and controls when the sign changes
        editableSign.onSignChanged(this::invalidateTree);
        // Create context
        this.context = new SignEditorContext(editableSign);
        this.context.onSelectedChanged(element -> this.invalidateTree());
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
                            .grow()
                            .crossAlign(Align.CENTER)
                    ),

                // Center editor
                box()
                    .grow()
                    .alignCenter()
                    .childGap(8)
                    .children(
                        new SignEditorView(this.context)
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
