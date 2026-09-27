package de.clickism.clicksigns.ui.components.sign;

import de.clickism.clicksigns.sign.element.SignElement;

/**
 * Interface for widgets that provide an element of a road sign.
 */
public interface ElementProvider {
    /**
     * Gets the current element.
     *
     * @return the current element.
     */
    SignElement element();
}
