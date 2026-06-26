package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.sign.element.SignElement;
import org.jetbrains.annotations.Nullable;

/**
 * Context for editing a road sign
 */
public class EditContext {
    @Nullable
    private SignElement selectedElement;
    private boolean dirty = false;

    public SignElement selectedElement() {
        return selectedElement;
    }

    public void selectElement(@Nullable SignElement selectedElement) {
        this.selectedElement = selectedElement;
        markDirty();
    }

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
