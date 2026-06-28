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

    protected final int anchorX;
    protected final int anchorY;

    public PlateWidget(int anchorX, int anchorY, PlateElement plate, ColorResolver colorResolver) {
        super(anchorX, anchorY, plate.front().resolve(colorResolver), GuiUtils.OUTLINE_COLOR);
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.plate = plate;
        this.colorResolver = colorResolver;
        this.updatePosition();
    }

    public void updatePosition() {
        // Calculate position
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, plate, this.width, this.height);
        this.setPosition(pos.x, pos.y);
    }

    @Override
    public SignElement element() {
        return plate;
    }
}
