package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clickui.event.events.DragEvent;
import de.clickism.clickui.event.events.DragStartEvent;
import de.clickism.clickui.event.events.KeyPressEvent;
import de.clickism.clickui.layout.Point;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static de.clickism.clicksigns.ui.UiConstants.UI_SCALE;

public class EditorActionHandler {
    /**
     * Global clipboard
     */
    private static final Set<Editable<SignElement>> CLIPBOARD = new HashSet<>();

    private final Map<Editable<SignElement>, Point> dragStartPositions = new HashMap<>();
    private final Map<Editable<SignElement>, Point> dragCurrentPositions = new HashMap<>();

    private boolean justDragged = false;

    private final SignEditorContext context;

    public EditorActionHandler(SignEditorContext context) {
        this.context = context;
    }

    /**
     * Handles key press events in the editor.
     *
     * @param event the key press event to handle
     */
    public void handleKeyPress(KeyPressEvent event) {
        // Duplicate selected elements with Ctrl+D
        if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_D) {
            forEachSelectedAndClearOld(element -> spawnElementNear(element, element));
            event.consume();
        }
        // Copy selected elements with Ctrl+C
        if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_C) {
            CLIPBOARD.clear();
            CLIPBOARD.addAll(context.selection());
            event.consume();
        }
        // Paste copied elements with Ctrl+V
        if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_V) {
            context.clearSelection();
            CLIPBOARD.forEach(element -> spawnElementNear(element, element));
            event.consume();
        }
        // Select all elements with Ctrl+A
        if (Screen.hasControlDown() && event.code() == GLFW.GLFW_KEY_A) {
            context.clearSelection();
            context.roadSign().elements().forEach(context::toggleSelection);
            event.consume();
        }
        // Delete selected elements with Delete key
        if (event.code() == GLFW.GLFW_KEY_DELETE) {
            // Don't use the delete key for text elements
            forEachSelectedAndClearOld(element -> {
                var current = element.current();
                if (current != null && !(current instanceof TextElement)) {
                    context.roadSign().removeElement(element.id());
                }
            });
            event.consume();
        }
    }

    public void handleDragStart(DragStartEvent event) {
        forEachSelected(element -> {
            var current = element.current();
            if (current != null) {
                dragStartPositions.put(element, new Point(current.x(), current.y()));
                dragCurrentPositions.put(element, new Point(current.x(), current.y()));
            }
        });
    }

    public void handleDrag(DragEvent event) {
        int deltaX = (int) (event.totalDeltaX() / UI_SCALE);
        int deltaY = (int) (event.totalDeltaY() / UI_SCALE);

        forEachSelected(element -> {
            var startPos = dragStartPositions.get(element);
            if (startPos == null) return;

            int newX = startPos.x() + deltaX;
            int newY = startPos.y() - deltaY; // Invert Y axis for screen coordinates
            dragCurrentPositions.put(element, new Point(newX, newY));

            var currentElement = element.current();
            if (currentElement == null) return;

            if (newX == currentElement.x() && newY == currentElement.y()) {
                // No change
                return;
            }

            justDragged = true;

            // Replace the element in the sign with a new one at the new position
            context.roadSign().updateElement(
                element.id(),
                updatedElement -> updatedElement.withPosition(newX, newY)
            );
        });
    }

    public void handleDragEnd() {
        dragStartPositions.clear();
        dragCurrentPositions.clear();
    }

    public void handleMouseDown(Editable<SignElement> element) {
        // If ctrl is held, toggle selection
        if (Screen.hasControlDown()) {
            context.setSelected(null, false); // So that no controls are shown
            context.toggleSelection(element);
        } else {
            // Don't clear selection yet, so that we can drag multiple elements
            var isSelected = context.isSelected(element);
            context.setSelected(element, !isSelected);
        }
    }

    public void handleMouseUp(Editable<SignElement> element) {
        // If ctrl is held, toggle selection
        if (!Screen.hasControlDown() && !justDragged) {
            // Clear selection and select the clicked element
            context.setSelected(element, true);
        }
        justDragged = false;
    }

    /**
     * Performs the given action on each selected element and clears the old selection.
     *
     * @param action the action to perform on each selected element
     */
    private void forEachSelectedAndClearOld(Consumer<Editable<SignElement>> action) {
        // Clear old selection and set new selection
        var newSelection = new HashSet<>(context.selection());
        context.clearSelection();
        newSelection.forEach(action);
    }

    /**
     * Performs the given action on each selected element.
     *
     * @param action the action to perform on each selected element
     */
    private void forEachSelected(Consumer<Editable<SignElement>> action) {
        context.selection().forEach(action);
    }

    private void spawnElementNearSelected(@Nullable Editable<SignElement> element) {
        spawnElementNear(element, context.selected());
    }

    private void spawnElementNear(@Nullable Editable<SignElement> element, @Nullable Editable<SignElement> other) {
        if (element == null) return;
        Point position;
        if (other == null) {
            position = context.roadSign().center();
        } else {
            var current = other.current();
            position = new Point(
                // Position the new element offset from the selected element, so they don't overlap
                (int) (current.x() + current.width() / 2),
                (int) (current.y() + current.height() / 2)
            );
        }
        var newElement = element.current().withPosition(position.x(), position.y());
        var editable = context.roadSign().addElement(newElement);
        context.setSelected(editable);
    }
}
