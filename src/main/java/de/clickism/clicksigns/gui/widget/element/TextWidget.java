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
    /**
     * Padding between the text and the edge of the sign, in pixels.
     * Used to calculate max width of text fields.
     */
    protected static final int SIGN_PADDING = 1;

    protected TextElement text;
    protected final ColorResolver colorResolver;
    protected int outlineColor;

    protected boolean renderOutlineOnHover = true;

    protected final int anchorX;
    protected final int anchorY;

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
        super(anchorX, anchorY, 0, 0, GuiUtils.font(), text.scale());
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        // Calculate dimensions
        this.height = (int) (TEXT_BOX_HEIGHT_SCALE * DEFAULT_TEXTURE_RENDER_SCALE * text.scale());
        // Calculate max width
        this.text = text;
        this.colorResolver = colorResolver;
        this.outlineColor = outlineColor;
        // Filter current text to fit and update
        this.value(clampString(text.text()));
        this.placeholder = clampString(this.placeholder);

        var textColor = colorResolver.resolveInt(text.color());
        var responder = FitIntoElementResponder.create(text, signWidth, this::value, this::textColor, textColor);
        this.addListener(responder::onChange);

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

    // TODO: Fix clamp string and max text width to work for all alignments and background colors, maybe make and use roadSign#withinBounds
    /**
     * Clamps the given string to fit within the max width of the text box.
     *
     * @param string the string to clamp
     * @return the clamped string that fits within the max width
     */
    protected String clampString(String string) {
//        while (text.guiWidthOf(string) > this.maxWidth && !string.isEmpty()) {
//            string = string.substring(0, string.length() - 1);
//        }
        return string;
    }

    @Override
    public SignElement element() {
        return text.withText(this.value());
    }

    /**
     * Calculates the max text width in sign pixels
     */
    public static int maxTextWidth(TextElement text, int signWidth) {
        return Integer.MAX_VALUE;
//        if (text.backgroundColor() != null) {
//            // If there is a background color, we need to account for the outline, which is 1 pixel wide
//            return signWidth - text.localX() - SIGN_PADDING - (int) (TEXT_PADDING_X * 2);
//        }
//        return signWidth - text.localX() - SIGN_PADDING;
// TODO: Half working, adapt:
//        int maxWidth;
//        if (text.alignment() == Alignment.TOP_CENTER) {
//            // If the text is center aligned, we return its current width
//            maxWidth = (int) Math.ceil(text.renderWidthOf(text.text()));
//        } else {
//            maxWidth = signWidth - text.localX() - SIGN_PADDING;
//        }
//        if (text.backgroundColor() != null) {
//            // If there is a background color, we need to account for the outline
//            maxWidth -= (int) (TEXT_PADDING_X * 2);
//        }
//        return maxWidth;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (BaseScreen.isHovered(this) && this.active && this.renderOutlineOnHover) {
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

    /**
     * Class that responds to changes in the text value and ensures a text fits within a text element's max width.
     */
    public record FitIntoElementResponder(
            TextElement text,
            int maxWidth,
            Consumer<String> valueSetter,
            Consumer<Integer> colorSetter,
            int defaultColor
    ) {
        /**
         * Handler for when the text value changes.
         *
         * @param value new value
         */
        public void onChange(String value) {
            if (text.guiWidthOf(value) > this.maxWidth) {
                // Text too big, trim it and set text color to red
                value = value.substring(0, value.length() - 1);
                this.valueSetter.accept(value);
                this.colorSetter.accept(GuiUtils.UNEDITABLE_COLOR);
            } else {
                this.colorSetter.accept(defaultColor);
            }
        }

        /**
         * Creates a new FitIntoElementResponder for the given text element and sign width.
         */
        public static FitIntoElementResponder create(
                TextElement text,
                int signWidth,
                Consumer<String> valueSetter,
                Consumer<Integer> colorSetter,
                int defaultColor
        ) {
            int maxWidth = maxTextWidth(text, signWidth);
            return new FitIntoElementResponder(text, maxWidth, valueSetter, colorSetter, defaultColor);
        }
    }
}
