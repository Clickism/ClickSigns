package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.sign.element.RoadSignElement;

/**
 * Interface for widgets that provide an element of a road sign.
 */
public interface ElementProvider {
    /**
     * Gets the current element.
     *
     * @return the current element.
     */
    RoadSignElement element();
}
