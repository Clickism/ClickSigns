package de.clickism.clicksigns.ui.editable;

import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.layout.Point;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Represents an editable road sign that can be modified and built into a road sign.
 * <p>
 * Uses {@link Editable} for its elements, allowing for modifications to individual elements,
 * while maintaining their identities.
 * <p>
 * Changes to the properties or elements of the road sign can be tracked using
 * {@link #onSignChanged(Runnable)}, which are notified whenever the sign changes.
 */
public class EditableRoadSign {
    private final Map<UUID, Editable<SignElement>> elements = new LinkedHashMap<>();
    private final List<Runnable> listeners = new ArrayList<>();
    private TextureSource frontSource;
    private TextureSource backSource;
    private Alignment alignment;

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

    /**
     * Gets the front texture source of the road sign.
     *
     * @return the front texture source
     */
    public TextureSource frontSource() {
        return frontSource;
    }

    /**
     * Sets the front texture source of the road sign.
     *
     * @param frontSource the new front texture source
     */
    public void frontSource(TextureSource frontSource) {
        this.frontSource = frontSource;
        matchPlateTextures();
        notifyListeners();
    }

    /**
     * Gets the back texture source of the road sign.
     *
     * @return the back texture source
     */
    public TextureSource backSource() {
        return backSource;
    }

    /**
     * Sets the back texture source of the road sign.
     *
     * @param backSource the new back texture source
     */
    public void backSource(TextureSource backSource) {
        this.backSource = backSource;
        matchPlateTextures();
        notifyListeners();
    }

    /**
     * Matches the textures of all plate elements in the road sign to the current front and back texture sources.
     */
    private void matchPlateTextures() {
        var sign = build();
        for (var element : elements.values()) {
            if (element.current() instanceof PlateElement plate && plate.matchSignTextures()) {
                element.update(current ->
                    plate.matchTextures(sign.frontSource(), sign.backSource()));
            }
        }
    }

    /**
     * Gets the collection of editable sign elements in the road sign.
     *
     * @return a collection of editable sign elements
     */
    public Collection<Editable<SignElement>> elements() {
        return elements.values();
    }

    /**
     * Gets the alignment of the road sign.
     *
     * @return the alignment of the road sign
     */
    public Alignment alignment() {
        return alignment;
    }

    /**
     * Sets the alignment of the road sign.
     *
     * @param alignment the new alignment of the road sign
     */
    public void alignment(Alignment alignment) {
        this.alignment = alignment;
        notifyListeners();
    }

    /**
     * Gets the editable sign element with the specified UUID.
     *
     * @param id the UUID of the sign element to retrieve
     * @return the editable sign element with the specified UUID, or null if not found
     */
    public Editable<SignElement> getElement(UUID id) {
        return elements.get(id);
    }

    /**
     * Moves the sign element with the specified UUID up in the rendering order.
     *
     * @param id the UUID of the sign element to move up
     */
    public void pushElementUp(UUID id) {
        if (id == null) return;
        var editable = elements.get(id);
        if (editable == null) return;
        elements.remove(id);
        var newMap = new LinkedHashMap<>(elements);
        newMap.put(id, editable);
        elements.clear();
        elements.putAll(newMap);
        notifyListeners();
    }

    /**
     * Updates the specified sign element using the provided updater function.
     *
     * @param id      the UUID of the sign element to update
     * @param updater a function that takes the current SignElement and returns an updated SignElement
     */
    public void updateElement(UUID id, UnaryOperator<SignElement> updater) {
        var editable = elements.get(id);
        if (editable != null) {
            editable.update(updater);
            notifyListeners();
        }
    }

    /**
     * Updates the specified sign element of a specific type using the provided updater function.
     *
     * @param id      the UUID of the sign element to update
     * @param type    the class type of the sign element to update
     * @param updater a function that takes the current SignElement and returns an updated SignElement
     * @param <T>     the type of the sign element to update
     */
    public <T extends SignElement> void updateElement(UUID id, Class<T> type, UnaryOperator<T> updater) {
        var editable = elements.get(id);
        if (editable != null && type.isInstance(editable.current())) {
            editable.update(element -> updater.apply(type.cast(element)));
            notifyListeners();
        }
    }

    /**
     * Updates the specified text element using the provided updater function.
     *
     * @param id      the UUID of the text element to update
     * @param updater a function that takes the current TextElement and returns an updated TextElement
     */
    public void updateTextElement(UUID id, UnaryOperator<TextElement> updater) {
        updateElement(id, TextElement.class, updater);
    }

    /**
     * Updates the specified symbol element using the provided updater function.
     *
     * @param id      the UUID of the symbol element to update
     * @param updater a function that takes the current SignElement and returns an updated SignElement
     */
    public void updateSymbolElement(UUID id, UnaryOperator<SymbolElement> updater) {
        updateElement(id, SymbolElement.class, updater);
    }

    /**
     * Updates the specified plate element using the provided updater function.
     *
     * @param id      the UUID of the plate element to update
     * @param updater a function that takes the current SignElement and returns an updated SignElement
     */
    public void updatePlateElement(UUID id, UnaryOperator<PlateElement> updater) {
        updateElement(id, PlateElement.class, updater);
    }

    /**
     * Removes the sign element with the specified UUID from the road sign.
     *
     * @param id the UUID of the sign element to remove
     */
    public void removeElement(UUID id) {
        notifyListeners();
        elements.remove(id);
    }

    /**
     * Adds a new sign element to the road sign.
     *
     * @param element the SignElement to add
     * @return the newly created EditableSignElement
     */
    public Editable<SignElement> addElement(SignElement element) {
        var editable = Editable.createRandom(element);
        elements.put(editable.id(), editable);
        notifyListeners();
        return editable;
    }

    /**
     * Regenerates the UUID of the sign element with the specified UUID.
     * The element will be replaced with a new EditableSignElement with a new UUID.
     * Useful if an element needs to be removed from caches/memo-s.
     *
     * @param id the UUID of the sign element to regenerate
     */
    public void regenerateId(UUID id) {
        var editable = elements.get(id);
        if (editable == null) return;
        var newEditable = Editable.createRandom(editable.current());
        elements.put(newEditable.id(), newEditable);
        elements.remove(id);
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
     * Gets the size of the road sign as a PixelSized object.
     *
     * @return the size of the road sign
     */
    public Size size() {
        return new Size(width(), height());
    }

    /**
     * Calculates the center point of the road sign based on its width and height.
     *
     * @return the center point of the road sign
     */
    public Point center() {
        return new Point(width() / 2, height() / 2);
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
        this.elements.clear();
        // Convert elements to editable elements
        for (SignElement element : roadSign.elements()) {
            var editable = Editable.createRandom(element);
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
            .map(Editable::current)
            .toList();
        return new RoadSign(frontSource, backSource, fixedElements, alignment);
    }
}
