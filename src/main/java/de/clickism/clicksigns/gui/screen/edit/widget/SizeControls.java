package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.widget.LazyEditBox;
import de.clickism.clicksigns.util.Size;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SizeControls extends NestedWidget {
    private final Size maxSize;
    private final EditBox widthBox;
    private final EditBox heightBox;
    private final Consumer<Size> onSizeChange;

    private Size size;

    public SizeControls(int x, int y, int width, Size initialSize, Size maxSize, Consumer<Size> onSizeChange) {
        super(x, y);
        this.size = initialSize;
        this.maxSize = maxSize;
        this.onSizeChange = onSizeChange;
        // Add widgets
        int gap = 4;
        int boxWidth = (width - gap) / 2;
        this.widthBox = new LazyEditBox(GuiUtils.font(), x, y, boxWidth, 20, Component.empty());
        this.heightBox = new LazyEditBox(GuiUtils.font(), x + boxWidth + gap, y, boxWidth, 20, Component.empty());

        this.widthBox.setResponder(value -> onChange(value, true));
        this.heightBox.setResponder(value -> onChange(value, false));
        this.widthBox.setValue(String.valueOf(initialSize.width()));
        this.heightBox.setValue(String.valueOf(initialSize.height()));

        addChild(this.widthBox);
        addChild(this.heightBox);
        updateSizeAndPosition();
    }

    public Size size() {
        return size;
    }

    protected @Nullable Integer getValidSize(String value, boolean isWidth) {
        try {
            int newValue = Integer.parseInt(value);
            int maxValue = isWidth ? maxSize.width() : maxSize.height();
            if (newValue < 1 || newValue > maxValue) {
                return null;
            }
            return newValue;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void onChange(String value, boolean isWidth) {
        var editBox = isWidth ? widthBox : heightBox;
        var newValue = getValidSize(value, isWidth);
        if (newValue == null) {
            // Invalid input
            editBox.setTextColor(GuiUtils.UNEDITABLE_COLOR);
            return;
        } else {
            // Valid size
            editBox.setTextColor(EditBox.DEFAULT_TEXT_COLOR);
        }

        // Update size and notify listener
        var oldSize = this.size;
        if (isWidth) {
            size = new Size(newValue, size.height());
        } else {
            size = new Size(size.width(), newValue);
        }
        if (size.equals(oldSize)) {
            return; // No change
        }
        onSizeChange.accept(size);
    }
}
