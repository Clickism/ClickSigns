package de.clickism.clicksigns.gui.screen.edit.widget.element;

import de.clickism.clicksigns.gui.screen.overview.widget.OverviewSymbolWidget;
import de.clickism.clicksigns.gui.util.LinearComposer;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.ColorBox;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.texture.source.ColorizedTextureSource;
import de.clickism.clicksigns.util.ComponentUtil;
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
    // TODO: Allow color replacement even if symbol doesnt by default
    public void compose() {
        // Texture
        var symbolWidget = new OverviewSymbolWidget(0, 0, symbolElement, colorResolver, parent);
        symbolWidget.onSymbolChanged(onUpdate);
        composer
                .header(Component.translatable("clicksigns.element.symbol"))
                .widget(symbolWidget);

        if (symbolElement.symbol().texture() instanceof ColorizedTextureSource colorized) {
            var fromColorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
            fromColorBox.setValue(colorized.fromColor());
            fromColorBox.setTooltip(ComponentUtil.tTooltip("clicksigns.editor.symbol.from_color"));

            var toColorBox = new ColorBox(0, 0, composer.width() - EDIT_BOX_OFFSET, 20, colorResolver);
            toColorBox.setValue(colorized.toColor());
            toColorBox.setTooltip(ComponentUtil.tTooltip("clicksigns.editor.symbol.to_color"));
            composer
                    .header(Component.translatable("clicksigns.editor.symbol.color_replacement"))
                    .widget(fromColorBox)
                    .widget(toColorBox)
                    .button(ComponentUtil.confirm(), button -> {
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
                .header(Component.translatable("clicksigns.editor.alignment"))
                .widget(AlignmentWidget.allAlignments(0, 0, symbolElement.alignment(), alignment -> {
                    onUpdate.accept(symbolElement.withAlignment(alignment));
                }));
    }

}
