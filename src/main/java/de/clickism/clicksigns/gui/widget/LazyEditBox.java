package de.clickism.clicksigns.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class LazyEditBox extends EditBox  {
    private @Nullable Consumer<String> responder;
    private String lastValue;

    public LazyEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
        this.lastValue = getValue();
        super.setResponder(this::handleResponse);
    }

    @Override
    public void setResponder(@Nullable Consumer<String> consumer) {
        this.responder = consumer;
    }

    protected void handleResponse(String value) {
        if (lastValue.equals(value)) {
            return; // No change
        }
        lastValue = value;
        // Call responder only if value is changed
        if (responder != null) {
            responder.accept(value);
        }
    }
}
