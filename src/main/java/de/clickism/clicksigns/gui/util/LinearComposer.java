package de.clickism.clicksigns.gui.util;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.CategoryHeaderWidget;
import de.clickism.clicksigns.gui.widget.ColoredButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.function.Consumer;

public class LinearComposer {
    private final LinearLayout layout;
    private final int width;

    public LinearComposer(LinearLayout layout, int width) {
        this.layout = layout;
        this.width = width;
    }

    public LinearComposer bigHeader(Component header) {
        layout.add(new CategoryHeaderWidget(this.width, 24, header));
        return this;
    }

    public LinearComposer header(Component header) {
        layout.add(new CategoryHeaderWidget(this.width, 16, header));
        return this;
    }

    public LinearComposer header(Component header, boolean style) {
        layout.add(new CategoryHeaderWidget(this.width, 16, header, style));
        return this;
    }

    public LinearComposer text(Component text) {
        layout.add(new StringWidget(0, 0, width, 16, text, GuiUtils.font()));
        return this;
    }

    public LinearComposer button(Component label, Button.OnPress onPress) {
        var button = Button.builder(label, onPress)
                .width(width)
                .build();
        layout.add(button);
        return this;
    }

    public LinearComposer coloredButton(Color color, Component label, Button.OnPress onPress) {
        var button = new ColoredButton(0, 0, width, 20, color, label, onPress);
        layout.add(button);
        return this;
    }

    public LinearComposer widget(AbstractWidget element) {
        layout.add(element);
        return this;
    }

    public LinearComposer spacing(int spacing) {
        layout.add(LinearLayout.spacer(spacing));
        return this;
    }

    public LinearComposer layout(int x, int y) {
        layout.layout(x, y);
        return this;
    }

    public void compose(Consumer<AbstractWidget> elementAdder) {
        for (var child : layout.children()) {
            if (child instanceof AbstractWidget widget) {
                elementAdder.accept(widget);
            }
        }
    }

    public int width() {
        return width;
    }
}
