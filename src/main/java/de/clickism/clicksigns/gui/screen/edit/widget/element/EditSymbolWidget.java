package de.clickism.clicksigns.gui.screen.edit.widget.element;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.EditContext;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SymbolElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.*;


public class EditSymbolWidget extends SymbolWidget {
    private final EditContext editContext;
    private final SymbolElement symbol;

    private static final Tooltip TOOLTIP = Tooltip.create(Component.literal("§f§lClick §rto select/deselect\n§f§lClick+drag §rto move element"));

    public EditSymbolWidget(int anchorX, int anchorY, SymbolElement symbol, ColorResolver colorResolver, Screen parent, EditContext editContext) {
        super(anchorX, anchorY, symbol, colorResolver, GuiUtils.OUTLINE_COLOR, parent);
        this.editContext = editContext;
        this.symbol = symbol;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        editContext.selectElement(this.symbol);
        editContext.dragging(true);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (editContext.dragging()) {
            this.setTooltip(null);
        } else {
            this.setTooltip(TOOLTIP);
        }
        var selected = this.symbol.equals(editContext.selectedElement());
        if (selected) {
            this.renderOutlineOnHover = false;
            GuiUtils.renderOutlineOnTop(guiGraphics, getX(), getY(), width, height, GuiUtils.SELECTED_OUTLINE_COLOR);
        } else {
            // Only render outline if nothing is being dragged
            this.renderOutlineOnHover = !editContext.dragging();
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (selected) {
            GuiUtils.renderPlusOnTop(guiGraphics, anchorX + symbol.localX() * DEFAULT_TEXTURE_RENDER_SCALE, anchorY - symbol.localY() * DEFAULT_TEXTURE_RENDER_SCALE, 5, Color.MAGENTA.getRGB());
        }
    }
}
