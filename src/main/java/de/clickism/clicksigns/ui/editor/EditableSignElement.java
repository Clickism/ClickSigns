package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.sign.element.SignElement;

import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Represents an editable sign element that can be modified while maintaining its identity.
 * <p>
 * Each EditableSignElement has a unique identifier (UUID) and holds a SignElement value.
 */
public class EditableSignElement {
    private final UUID id;
    private SignElement value;

    /**
     * Constructs a new EditableSignElement with the specified SignElement value.
     * A unique identifier (UUID) is generated for this instance.
     *
     * @param value the initial SignElement value
     */
    public EditableSignElement(SignElement value) {
        this.id = UUID.randomUUID();
        this.value = value;
    }

    /**
     * Returns the unique identifier (UUID) of this EditableSignElement.
     *
     * @return the UUID of this EditableSignElement
     */
    public UUID id() {
        return id;
    }

    /**
     * Returns the current SignElement value of this EditableSignElement.
     *
     * @return the current SignElement value
     */
    public SignElement current() {
        return value;
    }

    /**
     * Updates the SignElement value of this EditableSignElement using the provided updater function.
     *
     * @param updater the updater function that takes the current SignElement and returns a new SignElement
     * @return the updated SignElement value
     */
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
