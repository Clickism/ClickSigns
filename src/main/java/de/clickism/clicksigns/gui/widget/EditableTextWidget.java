/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package de.clickism.clicksigns.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class EditableTextWidget extends TextWidget {
    public EditableTextWidget(int width, int height, Text message, TextRenderer textRenderer) {
        super(width, height, message, textRenderer);
    }

    public EditableTextWidget(Text message, TextRenderer textRenderer) {
        super(message, textRenderer);
    }

    public void setText(Text text) {
        this.setMessage(text);
    }
}
