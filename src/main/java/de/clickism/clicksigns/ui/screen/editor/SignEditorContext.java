package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.ui.editable.EditableRoadSign;
import de.clickism.clicksigns.ui.editable.EditableSignElement;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SignEditorContext {
    private final EditableRoadSign roadSign;
    private @Nullable EditableSignElement selected = null;
    private final Set<EditableSignElement> selection = new HashSet<>();

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

    public Set<EditableSignElement> selection() {
        return selection;
    }

    public void toggleSelection(EditableSignElement element) {
        if (selection.contains(element)) {
            selection.remove(element);
        } else {
            selection.add(element);
        }
    }

    public void clearSelection() {
        selection.clear();
    }

    public boolean isSelected(EditableSignElement element) {
        return element.equals(selected) || selection.contains(element);
    }

    public void setSelected(@Nullable EditableSignElement selected) {
        setSelected(selected, true);
    }

    public void setSelected(@Nullable EditableSignElement selected, boolean clearSelection) {
        if (clearSelection) {
            this.selection.clear();
        }
        this.selected = selected;
        if (selected != null) {
            this.selection.add(selected);
        }
        this.onSelectedChanged.accept(selected);
    }

    public void onSelectedChanged(Consumer<EditableSignElement> listener) {
        this.onSelectedChanged = listener;
    }
}
