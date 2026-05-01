package de.clickism.clicksigns.sign.template.layout;

import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.util.Size;

import java.util.List;

/**
 * Layout interface for defining the arrangement of sign elements
 */
public interface Layout {
    /**
     * Builds the layout for the given size, returning a list of sign elements
     *
     * @param size the size of the sign for which to build the layout
     * @return a list of sign elements arranged according to this layout and given size
     */
    List<SignElement> build(Size size);

    /**
     * Gets the default size for this layout.
     * Used for template previews and defaults.
     *
     * @return the default size for this layout
     */
    Size defaultSize();
}
