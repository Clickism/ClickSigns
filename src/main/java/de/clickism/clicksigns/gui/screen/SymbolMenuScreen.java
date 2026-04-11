package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.widget.SymbolList;
import net.minecraft.network.chat.Component;

public class SymbolMenuScreen extends ScreenWithBackground {
    public SymbolMenuScreen() {
        super(Component.translatable("clicksigns.gui.symbol_menu.title"));
    }

    @Override
    protected void init() {
        var list = new SymbolList(this);
        addRenderableWidget(list);
    }
}
