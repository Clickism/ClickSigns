package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.gui.widget.texture.ClickableTextureWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;

public class PlateWidget extends ClickableTextureWidget implements ElementProvider {
    protected final ColorResolver colorResolver;
    protected PlateElement plate;

    public PlateWidget(int x, int y, PlateElement plate, ColorResolver colorResolver) {
        super(x, y, plate.front().resolve(colorResolver), GuiUtils.OUTLINE_COLOR);
        this.plate = plate;
        this.colorResolver = colorResolver;
    }

    @Override
    public SignElement element() {
        return plate;
    }
}
