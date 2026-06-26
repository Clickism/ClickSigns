package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.EditContext;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.awt.*;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

public class EditTextWidget extends TextWidget {
    private final EditContext editContext;
    private final TextElement text;

    public EditTextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth, EditContext editContext) {
        super(anchorX, anchorY, text, colorResolver, signWidth, GuiUtils.OUTLINE_COLOR);
        this.text = text;
        this.editable = false;
        this.editContext = editContext;
        setTooltip(Tooltip.create(Component.literal("§f§lClick §rto select/deselect\n§f§lClick+drag §rto move element")));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        editContext.selectElement(this.text);
        editContext.dragging(true);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean selected = this.text.equals(editContext.selectedElement());
        if (selected) {
            this.renderOutlineOnHover = false;
            renderOutline(guiGraphics, GuiUtils.SELECTED_OUTLINE_COLOR);
        } else if (!editContext.dragging()) {
            this.renderOutlineOnHover = true;
        }
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        GuiUtils.renderPlusOnTop(guiGraphics, anchorX + text.localX() * DEFAULT_TEXTURE_RENDER_SCALE, anchorY - text.localY() * DEFAULT_TEXTURE_RENDER_SCALE, 5, Color.MAGENTA.getRGB());
    }
}
