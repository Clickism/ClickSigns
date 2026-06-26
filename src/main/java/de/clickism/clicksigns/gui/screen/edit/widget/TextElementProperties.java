package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextElementProperties {

    public void compose(
            LinearComposer composer,
            int signWidth,
            TextElement textElement,
            Consumer<TextElement> onUpdate
    ) {
        // Text
        var textBox = new EditBox(GuiUtils.font(), 0, 0, 100, 20, Component.literal("Text"));
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
        var colorBox = new EditBox(GuiUtils.font(), 0, 0, 100, 20, Component.literal("Color"));
        colorBox.setValue(textElement.color());
        colorBox.setTooltip(Tooltip.create(Component.literal("Text Color")));
        var backgroundColorBox = new EditBox(GuiUtils.font(), 0, 0, 100, 20, Component.literal("Background Color"));
        backgroundColorBox.setValue(textElement.backgroundColor());
        backgroundColorBox.setTooltip(Tooltip.create(Component.literal("Background Color")));
        composer
                .header(Component.literal("Color"))
                .widget(colorBox)
                .widget(backgroundColorBox)
                .button(Component.literal("Confirm"), button -> {
                    onUpdate.accept(textElement.withColor(colorBox.getValue()).withBackgroundColor(backgroundColorBox.getValue()));
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
}
