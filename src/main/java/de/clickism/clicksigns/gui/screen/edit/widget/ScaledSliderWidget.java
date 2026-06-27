package de.clickism.clicksigns.gui.screen.edit.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

/**
 * Widget for a scaled slider
 */
class ScaledSliderWidget extends AbstractSliderButton {
    private final float minScale;
    private final float maxScale;
    private float scaleValue;

    public ScaledSliderWidget(int x, int y, int width, int height, float initialScale, float minScale, float maxScale) {
        super(x, y, width, height, Component.empty(), 0);
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.scaleValue = initialScale;
        // Set the initial message and value
        this.setMessage(formatCurrentScale(mapScaleToSlider(initialScale)));
        this.value = mapScaleToSlider(initialScale);
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

    private Component formatCurrentScale(double value) {
        return Component.literal(String.format("%.2f", mapSliderToScale(value)));
    }

    private float mapSliderToScale(double sliderValue) {
        return minScale + (maxScale - minScale) * (float) sliderValue;
    }

    private float mapScaleToSlider(float scale) {
        return (scale - minScale) / (maxScale - minScale);
    }
}
