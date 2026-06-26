package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextElementPropertiesComposer {

    /**
     * Adds the property controls for a TextElement to the given composer.
     *
     * @param composer    composer to add the controls to
     * @param signWidth   width of the sign, used to calculate max text width
     * @param textElement the TextElement to edit
     * @param onUpdate    callback to call when the TextElement is updated
     */
    public static void compose(
            LinearComposer composer,
            int signWidth,
            TextElement textElement,
            Consumer<TextElement> onUpdate
    ) {
        // Text
        var textBox = new EditBox(GuiUtils.font(), 0, 0, composer.width(), 20, Component.literal("Text"));
        textBox.setValue(textElement.text());
        var responder = TextWidget.FitIntoElementResponder.create(textElement, signWidth, textBox::setValue, textBox::setTextColor, EditBox.DEFAULT_TEXT_COLOR);
        textBox.setResponder(responder::onChange);

        composer
                .header(Component.literal("Text"))
                .widget(textBox)
                .button(Component.literal("Confirm"), button -> {
                    onUpdate.accept(textElement.withText(textBox.getValue()));
                });

        // Color
        var colorBox = new EditBox(GuiUtils.font(), 0, 0, composer.width(), 20, Component.literal("Color"));
        colorBox.setValue(textElement.color());
        colorBox.setTooltip(Tooltip.create(Component.literal("Text Color")));
        var backgroundColorBox = new EditBox(GuiUtils.font(), 0, 0, composer.width(), 20, Component.literal("Background Color"));
        backgroundColorBox.setValue(textElement.backgroundColor());
        backgroundColorBox.setTooltip(Tooltip.create(Component.literal("Background Color")));
        composer
                .header(Component.literal("Color"))
                .widget(colorBox)
                .widget(backgroundColorBox)
                .button(Component.literal("Confirm"), button -> {
                    onUpdate.accept(textElement
                            .withColor(colorBox.getValue())
                            .withBackgroundColor(backgroundColorBox.getValue())
                    );
                });

        // Scale
        var slider = new ScaleSliderWidget(0, 0, composer.width(), 20, textElement.scale());
        composer
                .header(Component.literal("Scale"))
                .widget(slider)
                .button(Component.literal("Confirm"), button -> {
                    float scale = slider.scaleValue();
                    onUpdate.accept(textElement.withScale(scale));
                });

        // Alignment
        composer
                .header(Component.literal("Alignment"))
                /*
                 TODO: Anything but right alignment is cursed when editing text and also max width isnt, that nice.
                 Maybe make a dynamic text edit box and make it dynamic when center aligned? idk... or adjust from the center?
                 */
                .widget(AlignmentWidget.textAlignments(0, 0, textElement.alignment(), alignment -> {
                    onUpdate.accept(textElement.withAlignment(alignment));
                }));
    }

    /**
     * Widget for a scale slider
     */
    private static class ScaleSliderWidget extends AbstractSliderButton {
        private static final float MAX_SCALE = 4f;
        private static final float MIN_SCALE = 0.3f;

        private float scaleValue;

        public ScaleSliderWidget(int x, int y, int width, int height, float initialScale) {
            super(x, y, width, height, formatCurrentScale(mapScaleToSlider(initialScale)), mapScaleToSlider(initialScale));
            this.scaleValue = initialScale;
        }

        @Override
        protected void updateMessage() {
            this.setMessage(formatCurrentScale(this.value));
        }

        @Override
        protected void applyValue() {
            scaleValue = mapSliderToScale(this.value);
        }

        public float scaleValue() {
            return scaleValue;
        }

        private static Component formatCurrentScale(double value) {
            return Component.literal(String.format("%.2f", mapSliderToScale(value)));
        }

        private static float mapSliderToScale(double sliderValue) {
            return MIN_SCALE + (MAX_SCALE - MIN_SCALE) * (float) sliderValue;
        }

        private static float mapScaleToSlider(float scale) {
            return (scale - MIN_SCALE) / (MAX_SCALE - MIN_SCALE);
        }
    }
}
