package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.screen.overview.widget.OverviewSymbolWidget;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.ColorBox;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.texture.source.ColorizedTextureSource;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Composes the property controls for a SymbolElement into a LinearComposer.
 *
 * @param composer      composer to add the controls to
 * @param parent        parent screen for the controls
 * @param colorResolver color resolver of the sign
 * @param symbolElement the SymbolElement to edit
 * @param onUpdate      callback to call when the SymbolElement is updated
 */
public record SymbolElementPropertiesComposer(
        LinearComposer composer,
        Screen parent,
        ColorResolver colorResolver,
        SymbolElement symbolElement,
        Consumer<SymbolElement> onUpdate
) {
    private static final int EDIT_BOX_OFFSET = 4;

    /**
     * Adds the property controls for the SymbolElement to the composer.
     */
    public void compose() {
        // Texture
        var symbolWidget = new OverviewSymbolWidget(0, 0, symbolElement, colorResolver, parent);
        symbolWidget.onSymbolChanged(onUpdate);
        composer
                .header(Component.literal("Symbol"))
                .widget(symbolWidget);

        // TODO: Fix color doesnt update when clicking confirm
        if (symbolElement.symbol().texture() instanceof ColorizedTextureSource colorized) {
            var fromColorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
            fromColorBox.setValue(colorized.fromColor());
            fromColorBox.setTooltip(Tooltip.create(Component.literal("Color to Replace")));

            var toColorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
            toColorBox.setValue(colorized.toColor());
            toColorBox.setTooltip(Tooltip.create(Component.literal("Color to Replace With")));
            composer
                    .header(Component.literal("Color Replacement"))
                    .widget(fromColorBox)
                    .widget(toColorBox)
                    .button(Component.literal("Confirm"), button -> {
                        var fromColor = fromColorBox.colorValueOrNull();
                        var toColor = toColorBox.colorValue();
                        var source = new ColorizedTextureSource(colorized.baseTexture(), fromColor, toColor);
                        var oldSymbol = symbolElement.symbol();
                        var newSymbol = new Symbol(oldSymbol.identifier(), source, oldSymbol.categoryId());
                        onUpdate.accept(symbolElement.withSymbol(newSymbol));
                    });
        }

        // Alignment
        composer
                .header(Component.literal("Alignment"))
                .widget(AlignmentWidget.allAlignments(0, 0, symbolElement.alignment(), alignment -> {
                    onUpdate.accept(symbolElement.withAlignment(alignment));
                }));
    }

}
