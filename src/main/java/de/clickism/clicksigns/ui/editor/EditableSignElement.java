package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.sign.element.SignElement;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class EditableSignElement {
    private final UUID id;
    private SignElement value;

    public EditableSignElement(SignElement value) {
        this.id = UUID.randomUUID();
        this.value = value;
    }

    public UUID id() {
        return id;
    }

    public SignElement current() {
        return value;
    }

    public SignElement update(UnaryOperator<SignElement> updater) {
        this.value = updater.apply(this.value);
        return this.value;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EditableSignElement other = (EditableSignElement) obj;
        return id.equals(other.id);
    }
}
