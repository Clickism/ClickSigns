package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.SymbolMenuScreen;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static de.clickism.clicksigns.gui.GuiUtils.OUTLINE_COLOR;

/**
 * Widget for a symbol element of a road sign
 */
public class SymbolElementWidget extends ClickableTextureWidget implements ElementProvider {
    private final int anchorX;
    private final int anchorY;
    private SymbolElement symbol;
    private final Screen parent;

    /**
     * Creates a new symbol widget.
     *
     * @param anchorX the x position to anchor the element on the sign
     * @param anchorY the y position to anchor the element on the sign
     * @param symbol  the symbol element to display
     * @param parent  the parent screen, used for going back from the symbol menu
     */
    public SymbolElementWidget(int anchorX, int anchorY, SymbolElement symbol, Screen parent) {
        super(anchorX, anchorY, symbol.texture(), OUTLINE_COLOR);
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.symbol = symbol;
        this.parent = parent;
        this.updatePosition();
        // TODO: Translate
        this.setTooltip(Tooltip.create(Component.literal("§f§lClick §rto cycle symbol\n§f§lRight click §rto open symbol menu")));
    }

    @Override
    public RoadSignElement element() {
        return symbol;
    }

    private void symbol(SymbolElement symbol) {
        this.symbol = symbol;
        this.texture(symbol.texture());
        updatePosition();
    }

    private void updatePosition() {
        var pos = GuiUtils.calculateElementPosition(anchorX, anchorY, symbol, this.width, this.height);
        this.setPosition(pos.x, pos.y);
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
        var symbolLocation = symbol.texture().location();
        var category = SymbolRegistry.categoryOf(symbolLocation);
        List<ResourceLocation> locations = SymbolRegistry.allInCategory(category);
        int currentIndex = locations.indexOf(symbolLocation);
        int nextIndex = (currentIndex + 1) % locations.size();
        // Update symbol
        this.symbol = this.symbol.withTexture(Texture.load(locations.get(nextIndex)));
        this.symbol(this.symbol);
    }

    private void openSymbolMenu() {
        GuiUtils.openScreen(new SymbolMenuScreen(parent, texture -> {
            this.symbol(this.symbol.withTexture(texture));
            GuiUtils.closeScreen();
        }));
    }
}
