package de.clickism.clicksigns.gui.screen.overview;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

/**
 * Text widget that can be edited by clicking
 */
public class OverviewTextWidget extends TextWidget {
    /**
     * Creates a new editable text widget.
     */
    public OverviewTextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth) {
        super(anchorX, anchorY, text, colorResolver, signWidth);
        // TODO: Translate
        // Add tooltip
        this.setTooltip(Tooltip.create(Component.literal("§lClick §rto edit text\n§lRight+Click §rto change variant")));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        if (this.isHovered && this.active) {
            GuiUtils.renderOutlineOnTop(guiGraphics, this.getX(), this.getY(), this.width, this.height, GuiUtils.OUTLINE_COLOR);
        }
    }
}
