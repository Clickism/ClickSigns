package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.sign.element.SignElement;
import org.jetbrains.annotations.Nullable;

/**
 * Context for editing a road sign
 */
public class EditContext {
    private @Nullable SignElement selectedElement;
    private boolean dirty = false;
    private boolean dragging = false;

    /**
     * Gets the current selected element for editing.
     *
     * @return the selected element, or null if none is selected
     */

    public @Nullable SignElement selectedElement() {
        return selectedElement;
    }

    /**
     * Selects an element for editing.
     *
     * @param selectedElement the element to select, or null to deselect
     */
    public void selectElement(@Nullable SignElement selectedElement) {
        this.selectedElement = selectedElement;
        markDirty();
    }

    public void dragging(boolean dragging) {
        this.dragging = dragging;
    }

    public boolean dragging() {
        return dragging;
    }

    // Dirty controls

    private void markDirty() {
        this.dirty = true;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }
}
