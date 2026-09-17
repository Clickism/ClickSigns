package de.clickism.clicksigns.ui.editable;

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
     * Constructs a new EditableSignElement with the specified id and SignElement value.
     *
     * @param id    the unique identifier (UUID) for this EditableSignElement
     * @param value the initial SignElement value
     */
    private EditableSignElement(UUID id, SignElement value) {
        this.id = id;
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

    /**
     * Creates a new EditableSignElement with a random UUID and the specified SignElement value.
     *
     * @param element the SignElement value for the new EditableSignElement
     * @return a new EditableSignElement instance with a random UUID and the specified SignElement value
     */
    public static EditableSignElement createRandom(SignElement element) {
        return new EditableSignElement(UUID.randomUUID(), element);
    }

    /**
     * Creates a new EditableSignElement with the specified UUID and SignElement value.
     *
     * @param id      the unique identifier (UUID) for the new EditableSignElement
     * @param element the SignElement value for the new EditableSignElement
     * @return a new EditableSignElement instance with the specified UUID and SignElement value
     */
    public static EditableSignElement of(UUID id, SignElement element) {
        return new EditableSignElement(id, element);
    }
}
