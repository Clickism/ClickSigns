package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.editable.EditableRoadSign;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SignEditorContext {
    private final EditableRoadSign roadSign;
    private @Nullable Editable<SignElement> selected = null;
    private final Set<Editable<SignElement>> selection = new HashSet<>();

    private Consumer<Editable<SignElement>> onSelectedChanged = element -> {};

    public SignEditorContext(EditableRoadSign roadSign) {
        this.roadSign = roadSign;
    }

    public EditableRoadSign roadSign() {
        return roadSign;
    }

    public @Nullable Editable<SignElement> selected() {
        return selected;
    }

    public Set<Editable<SignElement>> selection() {
        return selection;
    }

    public void toggleSelection(Editable<SignElement> element) {
        if (selection.contains(element)) {
            selection.remove(element);
        } else {
            selection.add(element);
        }
    }

    public void clearSelection() {
        selection.clear();
    }

    public boolean isSelected(Editable<SignElement> element) {
        return element.equals(selected) || selection.contains(element);
    }

    public void setSelected(@Nullable Editable<SignElement> selected) {
        setSelected(selected, true);
    }

    public void setSelected(@Nullable Editable<SignElement> selected, boolean clearSelection) {
        if (clearSelection) {
            this.selection.clear();
        }
        this.selected = selected;
        if (selected != null) {
            this.selection.add(selected);
        }
        this.onSelectedChanged.accept(selected);
    }

    public void onSelectedChanged(Consumer<Editable<SignElement>> listener) {
        this.onSelectedChanged = listener;
    }
}
