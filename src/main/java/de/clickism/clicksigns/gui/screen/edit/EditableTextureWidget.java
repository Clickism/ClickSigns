package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.gui.widget.texture.ClickableTextureWidget;
import de.clickism.clicksigns.sign.texture.Texture;

public class EditableTextureWidget extends ClickableTextureWidget {
    public EditableTextureWidget(int x, int y, Texture texture, int outlineColor) {
        super(x, y, texture, outlineColor, 2);
    }
}
