package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.screen.edit.EditContext;
import de.clickism.clicksigns.gui.widget.element.PlateWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

import static de.clickism.clicksigns.gui.widget.texture.TextureWidget.DEFAULT_TEXTURE_RENDER_SCALE;

/**
 * Widget that wraps an element widget and adds edit behavior to it, such as selection and dragging.
 * <p>
 * Only the edit behavior should be added to the screen, it will render the element widget itself.
 */
public class EditBehavior extends AbstractWidget {
    private static final Tooltip DEFAULT_TOOLTIP = Tooltip.create(Component.literal(
            "§f§lClick §rto select/deselect\n§f§lClick+drag §rto move element"
    ));

    private final EditContext editContext;
    private final int anchorX;
    private final int anchorY;
    private final AbstractWidget elementWidget;
    private final SignElement element;
    private final Tooltip tooltip;

    /**
     * Creates a new edit behavior widget.
     */
    public EditBehavior(
            EditContext editContext,
            int anchorX, int anchorY,
            AbstractWidget elementWidget,
            SignElement element,
            Tooltip tooltip
    ) {
        // Copy dimensions of element widget
        super(elementWidget.getX(), elementWidget.getY(), elementWidget.getWidth(), elementWidget.getHeight(), Component.empty());
        this.editContext = editContext;
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.elementWidget = elementWidget;
        this.element = element;
        this.tooltip = tooltip;
    }

    @Override
    public void onClick(double d, double e) {
        super.onClick(d, e);
        editContext.selectElement(this.element);
        editContext.dragging(true);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        boolean dragging = editContext.dragging();
        // Hide tooltip when dragging
        this.setTooltip(dragging ? null : tooltip);

        boolean selected = this.element.equals(editContext.selectedElement());
        boolean hovered = BaseScreen.isHovered(this, mouseX, mouseY);
        if (selected) {
            renderOutline(guiGraphics, GuiUtils.SELECTED_OUTLINE_COLOR);
        } else if (!dragging && hovered) {
            renderOutline(guiGraphics, GuiUtils.OUTLINE_COLOR);
        }
        this.elementWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        // Render plus at anchor position
        if (selected) {
            renderPlus(guiGraphics);
        }
    }

    private void renderPlus(GuiGraphics guiGraphics) {
        GuiUtils.renderPlusOnTop(guiGraphics,
                anchorX + element.localX() * DEFAULT_TEXTURE_RENDER_SCALE,
                anchorY - element.localY() * DEFAULT_TEXTURE_RENDER_SCALE,
                5, Color.MAGENTA.getRGB()
        );
    }

    private void renderOutline(GuiGraphics guiGraphics, int color) {
        // Render an outline around the widget
        GuiUtils.renderOutline(guiGraphics,
                getX() - 1,
                getY() - 1,
                width + 2,
                height + 2,
                color);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Nothing
    }

    public static EditBehavior forText(
            EditContext editContext,
            int anchorX,
            int anchorY,
            TextElement element,
            RoadSign roadSign
    ) {
        var widget = new TextWidget(anchorX, anchorY, element, roadSign.colorResolver(), roadSign.width(), 0);
        return new EditBehavior(editContext, anchorX, anchorY, widget, element, DEFAULT_TOOLTIP);
    }

    public static EditBehavior forSymbol(
            EditContext editContext,
            int anchorX,
            int anchorY,
            SymbolElement element,
            RoadSign roadSign,
            @Nullable Screen parent
    ) {
        var widget = new SymbolWidget(anchorX, anchorY, element, roadSign.colorResolver(), 0, parent);
        return new EditBehavior(editContext, anchorX, anchorY, widget, element, DEFAULT_TOOLTIP);
    }

    public static EditBehavior forPlate(
            EditContext editContext,
            int anchorX,
            int anchorY,
            PlateElement element,
            RoadSign roadSign,
            @Nullable Screen parent
    ) {
        var widget = new PlateWidget(anchorX, anchorY, element, roadSign.colorResolver());
        return new EditBehavior(editContext, anchorX, anchorY, widget, element, DEFAULT_TOOLTIP);
    }
}
