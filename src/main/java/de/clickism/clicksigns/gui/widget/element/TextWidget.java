package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_PADDING_X;
import static de.clickism.clicksigns.render.TextRenderer.TEXT_RENDER_SCALE;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Widget for a text element of a road sign
 */
public class TextWidget extends SignTextBox implements ElementProvider {
    protected static final int UNEDITABLE_COLOR = 0xFFFF5555;
    protected static final int TEXT_BOX_HEIGHT_SCALE = 4;
    /**
     * Padding between the text and the edge of the sign, in pixels.
     * Used to calculate max width of text fields.
     */
    protected static final int SIGN_PADDING = 1;

    protected TextElement text;
    protected final ColorResolver colorResolver;
    /**
     * Max text width in sign pixels
     */
    protected final int maxWidth;
    protected final int outlineColor;

    /**
     * Creates a new text element box.
     */
    public TextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth) {
        this(anchorX, anchorY, text, colorResolver, signWidth, GuiUtils.OUTLINE_COLOR);
    }

    /**
     * Creates a new text element box with a custom outline color.
     */
    public TextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth, int outlineColor) {
        // TODO: Maybe check other text fields to determine max width
        super(anchorX, anchorY,
                maxTextWidth(text, signWidth) * DEFAULT_TEXTURE_RENDER_SCALE,
                (int) (TEXT_BOX_HEIGHT_SCALE * DEFAULT_TEXTURE_RENDER_SCALE * text.scale()),
                GuiUtils.font(), text.scale());
        // Calculate max width
        this.maxWidth = maxTextWidth(text, signWidth);
        this.text = text;
        this.colorResolver = colorResolver;
        this.outlineColor = outlineColor;
        // Calculate position
        this.textColor(colorResolver.resolveInt(text.color()));
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, text, this.width, this.height);
        this.setPosition(pos.x, pos.y);

        // Filter current text to fit and update
        this.value(clampString(text.text()));
        this.placeholder = clampString(this.placeholder);

        this.onValueChanged(this::onChange);

        if (text.backgroundColor() != null) {
            this.backgroundColor(colorResolver.resolveInt(text.backgroundColor()));
        }
    }

    protected void onChange(String value) {
        if (renderWidthOf(value) > this.maxWidth) {
            // Text too big, trim it and set text color to red
            value = value.substring(0, value.length() - 1);
            this.value(value);
            this.textColor(UNEDITABLE_COLOR);
        } else {
            this.textColor(colorResolver.resolveInt(text.color()));
        }
    }

    /**
     * Clamps the given string to fit within the max width of the text box.
     *
     * @param string the string to clamp
     * @return the clamped string that fits within the max width
     */
    protected String clampString(String string) {
        while (renderWidthOf(string) > this.maxWidth && !string.isEmpty()) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    @Override
    public SignElement element() {
        return text.withText(this.value());
    }

    /**
     * Get the width of the text when rendered on a sign.
     *
     * @param string the text to measure.
     * @return the rendered width of the text.
     */
    private float renderWidthOf(String string) {
        return GuiUtils.font().width(string) * BLOCK_PIXELS * TEXT_RENDER_SCALE * this.text.scale();
    }

    /**
     * Calculates the max text width in sign pixels
     */
    private static int maxTextWidth(TextElement text, int signWidth) {
        if (text.backgroundColor() != null) {
            // If there is a background color, we need to account for the outline, which is 1 pixel wide
            return signWidth - text.localX() - SIGN_PADDING - (int) (TEXT_PADDING_X * 2);
        }
        return signWidth - text.localX() - SIGN_PADDING;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isHovered && this.active) {
            GuiUtils.renderOutlineOnTop(guiGraphics, this.getX() - 1, this.getY(), this.width + 2, this.height + 1, this.outlineColor);
        }
    }
}
