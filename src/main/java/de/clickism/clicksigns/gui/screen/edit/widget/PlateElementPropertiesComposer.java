package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Composes the property controls for a PlateElement into a LinearComposer.
 *
 * @param composer      composer to add the controls to
 * @param parent        parent screen for the controls
 * @param colorResolver color resolver of the sign
 * @param plateElement  the PlateElement to edit
 * @param onUpdate      callback to call when the PlateElement is updated
 */
public record PlateElementPropertiesComposer(
        LinearComposer composer,
        Screen parent,
        ColorResolver colorResolver,
        PlateElement plateElement,
        Consumer<PlateElement> onUpdate
) {
    /**
     * Adds the property controls for the PlateElement to the composer.
     */
    public void compose() {
        // Texture
        var textureWidget = new TexturePropertiesWidget(0, 0, parent, colorResolver,
                plateElement.front(), plateElement.back(),
                ((frontSource, backSource) -> {
                    var oldFront = plateElement.front().resolve(colorResolver);
                    onUpdate.accept(plateElement
                            .withFront(frontSource.resizeToFit(oldFront))
                            .withBack(backSource.resizeToFit(oldFront)));
                }));
        composer
                .header(Component.literal("Texture"))
                .widget(textureWidget);

        // Size
        var widthBox = new EditBox(GuiUtils.font(), 0, 0, composer.width() - 2, 20, Component.empty());
        var heightBox = new EditBox(GuiUtils.font(), 0, 0, composer.width() - 2, 20, Component.empty());
        widthBox.setValue(String.valueOf(plateElement.signWidth()));
        heightBox.setValue(String.valueOf(plateElement.signHeight()));
        composer
                .header(Component.literal("Size"))
                .widget(widthBox)
                .widget(heightBox)
                .button(Component.literal("Confirm"), button -> {
                    try {
                        var newWidth = Integer.parseInt(widthBox.getValue());
                        var newHeight = Integer.parseInt(heightBox.getValue());
                        onUpdate.accept(plateElement
                                .withFront(plateElement().front().resize(newWidth, newHeight))
                                .withBack(plateElement().back().resize(newWidth, newHeight)));
                    } catch (NumberFormatException e) {
                        // Ignore invalid input
                    }
                });

        // Alignment
        composer
                .header(Component.literal("Alignment"))
                .widget(AlignmentWidget.allAlignments(0, 0, plateElement.alignment(), alignment -> {
                    onUpdate.accept(plateElement.withAlignment(alignment));
                }));
    }

}
