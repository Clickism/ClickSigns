package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Represents an editable road sign that can be modified and built into a road sign.
 * <p>
 * Uses {@link EditableSignElement} for its elements, allowing for modifications to individual elements,
 * while maintaining their identities.
 * <p>
 * Changes to the properties or elements of the road sign can be tracked using
 * {@link #onSignChanged(Runnable)}, which are notified whenever the sign changes.
 */
public class EditableRoadSign {
    private final Map<UUID, EditableSignElement> elements = new LinkedHashMap<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private TextureSource frontSource;
    private TextureSource backSource;
    private Alignment alignment;
    private @Nullable ResourceLocation templateId;

    /**
     * Creates a new EditableRoadSign with the specified properties.
     *
     * @param roadSign the RoadSign to copy properties from
     */
    public EditableRoadSign(RoadSign roadSign) {
        this.copyFrom(roadSign);
    }

    /**
     * Registers a listener that will be notified whenever the sign changes.
     *
     * @param listener the listener to register
     */
    public void onSignChanged(Runnable listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners that the sign has changed.
     */
    protected void notifyListeners() {
        for (var callback : listeners) {
            callback.run();
        }
    }

    public TextureSource frontSource() {
        return frontSource;
    }

    public void frontSource(TextureSource frontSource) {
        this.frontSource = frontSource;
        notifyListeners();
    }

    public TextureSource backSource() {
        return backSource;
    }

    public void backSource(TextureSource backSource) {
        this.backSource = backSource;
        notifyListeners();
    }

    public Collection<EditableSignElement> elements() {
        return elements.values();
    }

    public Alignment alignment() {
        return alignment;
    }

    public void alignment(Alignment alignment) {
        this.alignment = alignment;
        notifyListeners();
    }

    public @Nullable ResourceLocation templateId() {
        return templateId;
    }

    public void templateId(@Nullable ResourceLocation templateId) {
        this.templateId = templateId;
        notifyListeners();
    }

    public void updateElement(UUID id, UnaryOperator<SignElement> updater) {
        var editable = elements.get(id);
        if (editable != null) {
            editable.update(updater);
            notifyListeners();
        }
    }

    public void removeElement(UUID id) {
        notifyListeners();
        elements.remove(id);
    }

    public void addElement(SignElement element) {
        var editable = new EditableSignElement(element);
        elements.put(editable.id(), editable);
        notifyListeners();
    }

    /**
     * Resizes the sign to the specified width and height.
     *
     * @param width  the new width of the sign
     * @param height the new height of the sign
     */
    public void resize(int width, int height) {
        copyFrom(build().resized(width, height));
    }

    /**
     * Gets the color resolver the road sign.
     *
     * @return the color resolver for the road sign
     */
    public ColorResolver colorResolver() {
        return frontSource.colorResolver();
    }

    /**
     * Gets the width of the road sign.
     *
     * @return the width of the road sign in pixels
     */
    public int width() {
        return frontSource.resolve(colorResolver()).width();
    }

    /**
     * Gets the height of the road sign.
     *
     * @return the height of the road sign in pixels
     */
    public int height() {
        return frontSource.resolve(colorResolver()).height();
    }

    /**
     * Copies the properties and elements from the given RoadSign into this EditableRoadSign.
     *
     * @param roadSign the RoadSign to copy from
     */
    public void copyFrom(RoadSign roadSign) {
        this.frontSource = roadSign.frontSource();
        this.backSource = roadSign.backSource();
        this.alignment = roadSign.alignment();
        this.templateId = roadSign.templateId();
        this.elements.clear();
        // Convert elements to editable elements
        for (SignElement element : roadSign.elements()) {
            var editable = new EditableSignElement(element);
            this.elements.put(editable.id(), editable);
        }
        notifyListeners();
    }

    /**
     * Builds a RoadSign instance from the current state of this EditableRoadSign.
     *
     * @return A new RoadSign instance with the current properties and elements.
     */
    public RoadSign build() {
        List<SignElement> fixedElements = elements.values().stream()
            .map(EditableSignElement::current)
            .toList();
        return new RoadSign(frontSource, backSource, fixedElements, alignment, templateId);
    }
}
