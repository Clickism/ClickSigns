package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class EditableRoadSign {
    private TextureSource frontSource;
    private TextureSource backSource;
    private final Map<UUID, EditableSignElement> elements = new LinkedHashMap<>();
    private Alignment alignment;
    private @Nullable ResourceLocation templateId;

    private final List<Consumer<Change>> eventListeners = new ArrayList<>();

    public EditableRoadSign(RoadSign roadSign) {
        this.loadSign(roadSign);
    }

    public void addChangeListener(Consumer<Change> listener) {
        eventListeners.add(listener);
    }

    protected void notifyListeners(Change change) {
        for (var listener : eventListeners) {
            listener.accept(change);
        }
    }

    public TextureSource frontSource() {
        return frontSource;
    }

    public void frontSource(TextureSource frontSource) {
        this.frontSource = frontSource;
    }

    public TextureSource backSource() {
        return backSource;
    }

    public void backSource(TextureSource backSource) {
        this.backSource = backSource;
    }

    public Collection<EditableSignElement> elements() {
        return elements.values();
    }

    public Alignment alignment() {
        return alignment;
    }

    public void alignment(Alignment alignment) {
        this.alignment = alignment;
    }

    public @Nullable ResourceLocation templateId() {
        return templateId;
    }

    public void templateId(@Nullable ResourceLocation templateId) {
        this.templateId = templateId;
    }

    public SignElement updateElement(UUID id, UnaryOperator<SignElement> updater) {
        var editable = elements.get(id);
        if (editable != null) {
            var value = editable.update(updater);
            notifyListeners(new Change.ElementUpdated(editable));
            return value;
        }
        return null;
    }

    public SignElement removeElement(UUID id) {
        notifyListeners(new Change.ElementRemoved(id));
        var editable = elements.remove(id);
        if (editable != null) {
            return editable.current();
        }
        return null;
    }

    public SignElement addElement(SignElement element) {
        var editable = new EditableSignElement(element);
        elements.put(editable.id(), editable);
        notifyListeners(new Change.ElementAdded(editable));
        return editable.current();
    }

    public void loadSign(RoadSign roadSign) {
        this.frontSource = roadSign.frontSource();
        this.backSource = roadSign.backSource();
        this.alignment = roadSign.alignment();
        this.templateId = roadSign.templateId();
        this.elements.clear();
        for (SignElement element : roadSign.elements()) {
            var editable = new EditableSignElement(element);
            this.elements.put(editable.id(), editable);
        }
        notifyListeners(new Change.Other());
    }

    public RoadSign build() {
        List<SignElement> elementList = elements.values().stream()
            .map(EditableSignElement::current)
            .toList();
        return new RoadSign(frontSource, backSource, elementList, alignment, templateId);
    }

    public sealed interface Change {
        record ElementAdded(EditableSignElement element) implements Change {}
        record ElementUpdated(EditableSignElement element) implements Change {}
        record ElementRemoved(UUID id) implements Change {}
        record Other() implements Change {}
    }
}
