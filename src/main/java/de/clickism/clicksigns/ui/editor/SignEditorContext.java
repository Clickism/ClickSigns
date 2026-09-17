package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.ui.editor.editable.EditableRoadSign;
import de.clickism.clicksigns.ui.editor.editable.EditableSignElement;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SignEditorContext {
    private final EditableRoadSign roadSign;
    private @Nullable EditableSignElement selected = null;

    private Consumer<EditableSignElement> onSelectedChanged = element -> {};

    public SignEditorContext(EditableRoadSign roadSign) {
        this.roadSign = roadSign;
    }

    public EditableRoadSign roadSign() {
        return roadSign;
    }

    public @Nullable EditableSignElement selected() {
        return selected;
    }

    public SignEditorContext setSelected(@Nullable EditableSignElement selected) {
        this.selected = selected;
        this.onSelectedChanged.accept(selected);
        return this;
    }

    public SignEditorContext onSelectedChanged(Consumer<EditableSignElement> listener) {
        this.onSelectedChanged = listener;
        return this;
    }

}
