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

    private static final Tooltip TOOLTIP = Tooltip.create(Component.literal("§f§lClick §rto select/deselect\n§f§lClick+drag §rto move element"));

    public EditTextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth, EditContext editContext) {
        super(anchorX, anchorY, text, colorResolver, signWidth, GuiUtils.OUTLINE_COLOR);
        this.text = text;
        this.editable = false;
        this.editContext = editContext;
        this.setTooltip(TOOLTIP);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        editContext.selectElement(this.text);
        editContext.dragging(true);
        this.setTooltip(null);
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        this.setTooltip(TOOLTIP);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (editContext.dragging()) {
            this.setTooltip(null);
        } else {
            this.setTooltip(TOOLTIP);
        }
        boolean selected = this.text.equals(editContext.selectedElement());
        if (selected) {
            this.renderOutlineOnHover = false;
            renderOutline(guiGraphics, GuiUtils.SELECTED_OUTLINE_COLOR);
        } else {
            // Only render outline if nothing is being dragged
            this.renderOutlineOnHover = !editContext.dragging();
        }

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        GuiUtils.renderPlusOnTop(guiGraphics, anchorX + text.localX() * DEFAULT_TEXTURE_RENDER_SCALE, anchorY - text.localY() * DEFAULT_TEXTURE_RENDER_SCALE, 5, Color.MAGENTA.getRGB());
    }
}
