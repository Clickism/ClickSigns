package de.clickism.clicksigns.gui.screen.overview.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.util.ComponentUtil;

/**
 * Text widget that can be edited by clicking
 */
public class OverviewTextWidget extends TextWidget {
    /**
     * Creates a new editable text widget.
     */
    public OverviewTextWidget(int anchorX, int anchorY, TextElement text, ColorResolver colorResolver, int signWidth) {
        super(anchorX, anchorY, text, colorResolver, signWidth, GuiUtils.OUTLINE_COLOR);
        this.setTooltip(ComponentUtil.translatableTooltip("clicksigns.overview.text.tooltip"));
    }
}
