package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.EditContext;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public class EditTextWidget extends TextWidget {
    private final EditContext editContext;

    public EditTextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth, EditContext editContext) {
        super(anchorX, anchorY, text, colorResolver, signWidth);
        setTooltip(Tooltip.create(Component.literal("§f§lClick §rto select/deselect\n§f§lClick+drag §rto move element")));
        this.editable = false;
        this.editContext = editContext;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        editContext.selectElement(this.text);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.text.equals(editContext.selectedElement())) {
            renderOutline(guiGraphics, GuiUtils.SELECTED_OUTLINE_COLOR);
        }
    }
}
