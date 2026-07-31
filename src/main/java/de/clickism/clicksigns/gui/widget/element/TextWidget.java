package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;

import java.util.function.Consumer;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * Widget for a text element of a road sign
 */
public class TextWidget extends SignTextBox implements ElementProvider {
    protected static final int TEXT_BOX_HEIGHT_SCALE = 4;

    protected TextElement text;
    protected final ColorResolver colorResolver;
    protected int outlineColor;

    // TODO: Refactor outline rendering logic
    protected boolean renderOutlineOnHover = true;

    protected final int anchorX;
    protected final int anchorY;

    /**
     * Creates a new text element box with a custom outline color.
     */
    public TextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth, int outlineColor) {
        super(anchorX, anchorY, 0, 0, GuiUtils.font(), text.scale());
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        // Calculate dimensions
        this.height = (int) (TEXT_BOX_HEIGHT_SCALE * DEFAULT_TEXTURE_RENDER_SCALE * text.scale());
        // Calculate max width
        this.text = text;
        this.colorResolver = colorResolver;
        this.outlineColor = outlineColor;
        // Set current text
        this.value(text.text());

        // Set up colors
        var textColor = colorResolver.resolveInt(text.color());
        this.textColor(textColor);
        if (text.backgroundColor() != null) {
            this.backgroundColor(colorResolver.resolveInt(text.backgroundColor()));
        }

        // Calculate dimensions and position
        this.updateWidth();
    }

    @Override
    protected void updateWidth() {
        super.updateWidth();
        // Update position after width change
        this.updatePosition();
    }

    /**
     * Updates the position of the text box based on the anchor and the size of the text.
     */
    public void updatePosition() {
        // Calculate position
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, text, this.width, this.height);
        this.setPosition(pos.x, pos.y);
    }

    @Override
    public SignElement element() {
        return text.withText(this.value());
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (BaseScreen.isHovered(this, mouseX, mouseY) && this.active && this.renderOutlineOnHover) {
            renderOutline(guiGraphics, outlineColor);
        }
    }

    /**
     * Renders an outline around the text box.
     *
     * @param guiGraphics  the GuiGraphics to render with
     * @param outlineColor the color of the outline
     */
    protected void renderOutline(GuiGraphics guiGraphics, int outlineColor) {
        var currentWidth = currentWidth();
        GuiUtils.renderOutlineOnTop(guiGraphics, this.getX() - 1, this.getY(), currentWidth + 2, this.height + 1, outlineColor);
    }
}
