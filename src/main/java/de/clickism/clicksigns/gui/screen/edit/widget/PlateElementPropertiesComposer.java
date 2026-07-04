package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.util.Size;
import de.clickism.clicksigns.util.ComponentUtil;
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
    private static final Size MAX_SIZE = new Size(96, 64);

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
                .header(Component.translatable("clicksigns.editor.texture"))
                .widget(textureWidget);

        // Size
        var sizeControls = new SizeControls(0, 0, composer.width() - 2, plateElement.signSize(), MAX_SIZE, size -> {});
        composer
                .header(Component.translatable("clicksigns.editor.size"))
                .widget(sizeControls)
                .button(ComponentUtil.confirm(), button -> {
                    var size = sizeControls.size();
                    onUpdate.accept(plateElement
                            .withFront(plateElement.front().resizeToFit(size))
                            .withBack(plateElement.back().resizeToFit(size))
                    );
                });
        // Alignment
        composer
                .header(Component.translatable("clicksigns.editor.alignment"))
                .widget(AlignmentWidget.allAlignments(0, 0, plateElement.alignment(), alignment -> {
                    onUpdate.accept(plateElement.withAlignment(alignment));
                }));
    }

}
