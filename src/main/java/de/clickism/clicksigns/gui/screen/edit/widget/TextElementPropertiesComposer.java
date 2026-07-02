package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.ColorBox;
import de.clickism.clicksigns.gui.widget.LazyEditBox;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Composes the property controls for a TextElement into a LinearComposer.
 *
 * @param composer      composer to add the controls to
 * @param signWidth     width of the sign, used to calculate max text width
 * @param colorResolver color resolver of the sign
 * @param textElement   the TextElement to edit
 * @param onUpdate      callback to call when the TextElement is updated
 */
public record TextElementPropertiesComposer(
        LinearComposer composer,
        int signWidth,
        ColorResolver colorResolver,
        TextElement textElement,
        Consumer<TextElement> onUpdate
) {
    private static final float MAX_SCALE = 4f;
    private static final float MIN_SCALE = 0.3f;

    private static final int EDIT_BOX_OFFSET = 4;

    // TODO: Maybe make values update on change instead of having to click confirm
    /**
     * Adds the property controls for the TextElement to the composer.
     */
    public void compose() {
        // Text
        var textBox = new LazyEditBox(GuiUtils.font(), 0, 0, composer.width() - EDIT_BOX_OFFSET, 20, Component.literal("Text"));
        textBox.setValue(textElement.text());
        composer
                .header(Component.literal("Text"))
                .widget(textBox)
                .button(Component.literal("Confirm"), button -> {
                    onUpdate.accept(textElement.withText(textBox.getValue()));
                });

        // Color
        var colorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
        colorBox.setValue(textElement.color());
        colorBox.setTooltip(Tooltip.create(Component.literal("Text Color")));

        var backgroundColorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
        backgroundColorBox.setValue(textElement.backgroundColor());
        backgroundColorBox.setTooltip(Tooltip.create(Component.literal("Background Color")));
        composer
                .header(Component.literal("Color"))
                .widget(colorBox)
                .widget(backgroundColorBox)
                .button(Component.literal("Confirm"), button -> {
                    onUpdate.accept(textElement
                            .withColor(colorBox.colorValue())
                            .withBackgroundColor(backgroundColorBox.colorValueOrNull())
                    );
                });

        // Scale
        var slider = new ScaledSliderWidget(0, 0, composer.width(), 20, textElement.scale(), MIN_SCALE, MAX_SCALE);
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
                .widget(AlignmentWidget.textAlignments(0, 0, textElement.alignment(), alignment -> {
                    onUpdate.accept(textElement.withAlignment(alignment));
                }));
    }

}
