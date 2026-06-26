package de.clickism.clicksigns.gui.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.TextElement;

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
        this.value(filterInput(text.text()));
        this.placeholder = filterInput(this.placeholder);

        if (text.backgroundColor() != null) {
            this.backgroundColor(colorResolver.resolveInt(text.backgroundColor()));
        }
    }

    @Override
    protected String filterInput(String input) {
        String newValue = value + input;
        // Trim input to fit in max width
        boolean trimmed = false;
        while (renderWidthOf(newValue) > this.maxWidth && !newValue.isEmpty()) {
            input = input.substring(0, input.length() - 1);
            newValue = value + input;
            trimmed = true;
        }
        // Adjust color
        if (trimmed) {
            this.textColor(UNEDITABLE_COLOR);
        } else {
            this.textColor(colorResolver.resolveInt(text.color()));
        }
        return input;
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

}
