package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.widget.*;
import net.minecraft.network.chat.Component;

public class SymbolMenuScreen extends ScreenWithBackground {
    public SymbolMenuScreen() {
        super(Component.translatable("clicksigns.gui.symbol_menu.title"));
    }

    @Override
    protected void init() {
        int listWidth = Math.min(400, this.width);
        var list = new SymbolList(this.width / 2 - listWidth / 2, 20, listWidth, this.height - 40);
        addRenderableWidget(list);
    }
}
