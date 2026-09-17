package de.clickism.clicksigns.ui.components.sign;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.editable.EditableRoadSign;
import de.clickism.clicksigns.ui.editable.Editable;
import de.clickism.clicksigns.ui.screen.texture.TextureList;
import de.clickism.clicksigns.ui.screen.texture.TextureSelectScreen;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.event.events.MouseClickEvent;

import static de.clickism.clicksigns.util.ComponentUtil.l;

public class SymbolView extends UiComponent<SymbolView>
    implements ElementProvider {

    private final SymbolElement element;
    private final ColorResolver colorResolver;

    public SymbolView(SymbolElement element, ColorResolver colorResolver) {
        this.element = element;
        this.colorResolver = colorResolver;
    }

    /**
     * Handles the symbol change logic for a given sign and symbol element based on mouse click events.
     * <p>
     * If the left mouse button is clicked, it cycles to the next symbol in the same category.
     * <p>
     * If the right mouse button is clicked, it opens a symbol selection menu allowing the
     * user to choose a new symbol from the available options.
     *
     * @param sign          The editable road sign containing the symbol element.
     * @param symbolElement The symbol element to be updated.
     * @param event         The mouse click event that triggered the symbol change.
     */
    public static void handleSymbolChange(
        EditableRoadSign sign,
        Editable<SignElement> symbolElement,
        MouseClickEvent event
    ) {
        var current = symbolElement.current();
        if (!(current instanceof SymbolElement symbol)) return;

        if (event.isLeftClick()) {
            // Cycle to next symbol in the same category
            var nextSymbol = symbol.symbol().nextInCategory();
            sign.updateSymbolElement(
                symbolElement.id(),
                element -> element.withSymbol(nextSymbol)
            );
        }

        if (event.isRightClick()) {
            // Open symbol menu
            var colorResolver = sign.colorResolver();
            var entries = SignRegistries.SYMBOLS.all().stream()
                .map(s -> new TextureList.Entry(
                    s.texture().resolve(colorResolver),
                    s.identifier(),
                    s.resolveCategory()
                ))
                .toList();

            // Find sign background primary color
            var backgroundColor = UiUtil.primaryColorOf(sign.frontSource().resolveImage(sign.colorResolver()));
            new TextureSelectScreen(l("Select Symbol"), entries, backgroundColor)
                .textureScale(3.0f)
                .onTextureSelected(entry -> {
                    var newSymbol = SignRegistries.SYMBOLS.get(entry.identifier());
                    if (newSymbol == null) return;
                    sign.updateSymbolElement(
                        symbolElement.id(),
                        element -> element.withSymbol(newSymbol)
                    );
                }).open();
        }
    }

    @Override
    protected void build() {
        var texture = element.symbol().texture().resolve(colorResolver);
        add(UiUtil.imageOf(texture));
    }

    @Override
    public SignElement element() {
        return element;
    }
}
