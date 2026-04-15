package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.widget.*;
import de.clickism.clicksigns.sign.texture.Texture;
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

    private final Consumer<Texture> onSymbolSelected;

    /**
     * Creates a new symbol menu screen.
     *
     * @param onSymbolSelected callback for when a symbol is selected, receives the selected symbol's texture
     */
    public SymbolMenuScreen(Screen parent, Consumer<Texture> onSymbolSelected) {
        super(parent);
        this.onSymbolSelected = onSymbolSelected;
    }

    @Override
    protected void init() {
        int listWidth = Math.min(MAX_SYMBOL_LIST_WIDTH, this.width / 2 - 20);
        int listHeight = this.height - MARGIN_TOP - MARGIN_BOTTOM;
        int listX = this.width / 2 - listWidth / 2;
        var list = new SymbolList(listX, MARGIN_TOP, listWidth, listHeight, onSymbolSelected);
        addRenderableWidget(list);
    }
}
