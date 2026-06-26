package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.EditContext;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.SymbolElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


public class EditSymbolWidget extends SymbolWidget {
    private final EditContext editContext;

    public EditSymbolWidget(int anchorX, int anchorY, SymbolElement symbol, ColorResolver colorResolver, Screen parent, EditContext editContext) {
        super(anchorX, anchorY, symbol, colorResolver, GuiUtils.OUTLINE_COLOR, parent);
        setTooltip(Tooltip.create(Component.literal("§f§lClick §rto select/deselect\n§f§lClick+drag §rto move element")));
        this.editContext = editContext;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        editContext.selectElement(this.symbol);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.symbol.equals(editContext.selectedElement())) {
            GuiUtils.renderOutlineOnTop(guiGraphics, getX(), getY(), width, height, GuiUtils.SELECTED_OUTLINE_COLOR);
        }
    }
}
