package de.clickism.clicksigns.gui.screen.overview.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.TextureMenuScreen;
import de.clickism.clicksigns.gui.widget.TextureList;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.util.ComponentUtil;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Symbol widget that can be edited/cycled by clicking
 */
public class OverviewSymbolWidget extends SymbolWidget {
    private @Nullable Consumer<SymbolElement> onSymbolChanged;

    /**
     * Creates a new editable symbol widget.
     */
    public OverviewSymbolWidget(int anchorX, int anchorY, SymbolElement symbol, ColorResolver colorResolver, Screen parent) {
        super(anchorX, anchorY, symbol, colorResolver, GuiUtils.OUTLINE_COLOR, parent);
        this.setTooltip(ComponentUtil.tTooltip("clicksigns.overview.symbol.tooltip"));
    }

    /**
     * Sets the callback for when the symbol is changed.
     *
     * @param onSymbolChanged callback to call when the symbol is changed
     */
    public void onSymbolChanged(Consumer<SymbolElement> onSymbolChanged) {
        this.onSymbolChanged = onSymbolChanged;
    }

    @Override
    protected boolean isValidClickButton(int i) {
        return GuiUtils.isLeftClick(i) || GuiUtils.isRightClick(i);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) return false;
        if (GuiUtils.isLeftClick(button)) {
            // Cycle to next symbol in the same category
            var nextSymbol = symbol.symbol().nextInCategory();
            this.symbol(this.symbol.withSymbol(nextSymbol));
            triggerSymbolChanged();
            return true;
        }
        // Right click
        if (GuiUtils.isRightClick(button)) {
            // Open symbol menu
            // TODO: Add uncategorized symbols at the end
            var categoryToTextures = SignRegistries.SYMBOLS.categoryToEntriesAndThen(symbol -> new TextureList.IdentifiableTexture(
                    symbol.identifier(),
                    symbol.texture().resolve(colorResolver)));
            // Open symbol selector screen
            var screen = new TextureMenuScreen<>(parent, categoryToTextures, identifier -> {
                var symbol = SignRegistries.SYMBOLS.get(identifier);
                if (symbol == null) return;
                this.symbol(this.symbol.withSymbol(symbol));
                triggerSymbolChanged();
                GuiUtils.closeScreen();
            });
            GuiUtils.openScreen(screen);
            return true;
        }
        return false;
    }

    private void triggerSymbolChanged() {
        if (onSymbolChanged == null) return;
        onSymbolChanged.accept(this.symbol);
    }
}
