package de.clickism.clicksigns.gui.screen.edit.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.ColorBox;
import de.clickism.clicksigns.gui.widget.LazyEditBox;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.util.ComponentUtil;
import net.minecraft.client.gui.components.EditBox;
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
    private static final float MAX_SCALE = 6f;
    private static final float MIN_SCALE = 0.3f;

    private static final int EDIT_BOX_OFFSET = 4;

    // TODO: Maybe make values update on change instead of having to click confirm

    /**
     * Adds the property controls for the TextElement to the composer.
     */
    public void compose() {
        // Text
        var textBox = new LazyEditBox(GuiUtils.font(), 0, 0, composer.width() - EDIT_BOX_OFFSET, 20, Component.empty());
        textBox.setValue(textElement.text());
        composer
                .header(Component.translatable("clicksigns.element.text"))
                .widget(textBox)
                .button(ComponentUtil.confirm(), button -> {
                    onUpdate.accept(textElement.withText(textBox.getValue()));
                });

        // Color
        var colorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
        colorBox.setValue(textElement.color());
        colorBox.setTooltip(ComponentUtil.tTooltip("clicksigns.editor.text.text_color"));

        var backgroundColorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
        backgroundColorBox.setValue(textElement.backgroundColor());
        backgroundColorBox.setTooltip(ComponentUtil.tTooltip("clicksigns.editor.text.background_color"));
        composer
                .header(Component.translatable("clicksigns.editor.text.color"))
                .widget(colorBox)
                .widget(backgroundColorBox)
                .button(ComponentUtil.confirm(), button -> {
                    onUpdate.accept(textElement
                            .withColor(colorBox.colorValue())
                            .withBackgroundColor(backgroundColorBox.colorValueOrNull())
                    );
                });

        // Scale
        var scaleBox = new LazyEditBox(GuiUtils.font(), 0, 0, composer.width() - EDIT_BOX_OFFSET, 20, Component.empty());
        scaleBox.setValue(String.valueOf(textElement.scale()));
        scaleBox.setResponder(value -> {
            try {
                float scale = Float.parseFloat(value);
                if (scale < MIN_SCALE || scale > MAX_SCALE) {
                    scaleBox.setTextColor(GuiUtils.UNEDITABLE_COLOR);
                } else {
                    scaleBox.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
                }
            } catch (NumberFormatException e) {
                scaleBox.setTextColor(GuiUtils.UNEDITABLE_COLOR);
            }
        });
        composer
                .header(Component.translatable("clicksigns.editor.text.scale"))
                .widget(scaleBox)
                .button(ComponentUtil.confirm(), button -> {
                    try {
                        float scale = Float.parseFloat(scaleBox.getValue());
                        if (scale < MIN_SCALE || scale > MAX_SCALE) {
                            return;
                        }
                        onUpdate.accept(textElement.withScale(scale));
                    } catch (NumberFormatException ignored) {
                    }
                });

        // Alignment
        composer
                .header(Component.translatable("clicksigns.editor.alignment"))
                .widget(AlignmentWidget.textAlignments(0, 0, textElement.alignment(), alignment -> {
                    onUpdate.accept(textElement.withAlignment(alignment));
                }));
    }

}
