package de.clickism.clicksigns.gui.screen.overview;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.symbol.SymbolMenuScreen;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SymbolElement;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Symbol widget that can be edited/cycled by clicking
 */
public class EditableSymbolWidget extends SymbolWidget {
    /**
     * Creates a new editable symbol widget.
     */
    public EditableSymbolWidget(int anchorX, int anchorY, SymbolElement symbol, ColorResolver colorResolver, Screen parent) {
        super(anchorX, anchorY, symbol, colorResolver, GuiUtils.OUTLINE_COLOR, parent);
        // TODO: Translate
        this.setTooltip(Tooltip.create(Component.literal("§f§lClick §rto cycle symbol\n§f§lRight click §rto open symbol menu")));
    }

    @Override
    protected boolean isValidClickButton(int i) {
        // Left or right click
        return i == 0 || i == 1;
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (!super.mouseClicked(d, e, i)) return false;
        // Left click
        if (i == 0) {
            cycleSymbol();
            return true;
        }
        // Right click
        if (i == 1) {
            openSymbolMenu();
            return true;
        }
        return false;
    }

    private void cycleSymbol() {
        var category = symbol.symbol().resolveCategory();
        if (category == null) return;
        List<ResourceLocation> ids = new ArrayList<>(category.entries());
        int currentIndex = ids.indexOf(symbol.symbol().identifier());
        int nextIndex = (currentIndex + 1) % ids.size();
        // Update symbol
        this.symbol = this.symbol.withSymbol(SignRegistries.SYMBOLS.get(ids.get(nextIndex)));
        this.symbol(this.symbol);
    }

    private void openSymbolMenu() {
        GuiUtils.openScreen(new SymbolMenuScreen(parent, colorResolver, symbol -> {
            this.symbol(this.symbol.withSymbol(symbol));
            GuiUtils.closeScreen();
        }));
    }
}
