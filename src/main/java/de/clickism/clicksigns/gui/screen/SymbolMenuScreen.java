package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.widget.*;
import de.clickism.clicksigns.sign.texture.Symbol;
import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.Consumer;

/**
 * Screen for selecting a symbol to place on a sign.
 * Displays a scrollable list of symbols, organized by category.
 */
public class SymbolMenuScreen extends BaseScreen {
    private static final int MAX_SYMBOL_LIST_WIDTH = 400;
    private static final int MARGIN_TOP = 20;
    private static final int MARGIN_BOTTOM = 20;

    private final ColorResolver colorResolver;
    private final Consumer<Symbol> onSymbolSelected;

    /**
     * Creates a new symbol menu screen.
     *
     * @param parent           the parent screen to return to when closing this screen
     * @param colorResolver    the color resolver to use for resolving symbol textures
     * @param onSymbolSelected callback for when a symbol is selected, receives the selected symbol's texture
     */
    public SymbolMenuScreen(Screen parent, ColorResolver colorResolver, Consumer<Symbol> onSymbolSelected) {
        super(parent);
        this.colorResolver = colorResolver;
        this.onSymbolSelected = onSymbolSelected;
    }

    @Override
    protected void init() {
        int listWidth = Math.min(MAX_SYMBOL_LIST_WIDTH, this.width / 2 - 20);
        int listHeight = this.height - MARGIN_TOP - MARGIN_BOTTOM;
        int listX = this.width / 2 - listWidth / 2;
        var list = new SymbolList(listX, MARGIN_TOP, listWidth, listHeight, colorResolver, onSymbolSelected);
        addRenderableWidget(list);
    }
}
