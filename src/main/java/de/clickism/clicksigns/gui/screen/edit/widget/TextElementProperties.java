package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextElementProperties {

    public void compose(
            LinearComposer composer,
            int signWidth,
            TextElement textElement,
            Consumer<TextElement> onUpdate
    ) {
        composer
                .header(Component.literal("Text"));

        var editBox = new EditBox(GuiUtils.font(), 0, 0, 100, 20, Component.literal("Text"));
        editBox.setValue(textElement.text());
        var responder = TextWidget.FitIntoElementResponder.create(textElement, signWidth, editBox::setValue, editBox::setTextColor, EditBox.DEFAULT_TEXT_COLOR);
        editBox.setResponder(responder::onChange);

        composer
                .widget(editBox)
                .button(Component.literal("Confirm"), button -> {
                    onUpdate.accept(textElement.withText(editBox.getValue()));
                })
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
