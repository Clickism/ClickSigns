package de.clickism.clicksigns.ui.components;

import de.clickism.clickui.State;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.style.Style;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A UI component that allows the user to select a number within a specified range
 * with increment and decrement buttons.
 */
public class NumberControl extends UiComponent<NumberControl> implements CommonComponents {
    private final State<Integer> value = state(0);
    private int minValue = 0;
    private int maxValue = Integer.MAX_VALUE;

    private int changeAmount = 1;
    private int fineChangeAmount = 0;
    private int fastChangeAmount = 0;

    private Component unit = l("px");

    private Consumer<Integer> onValueChanged = value -> {};

    private int height = 14; // Default height
    private boolean allowInput = true;

    public NumberControl value(int value) {
        this.value.update(value);
        return this;
    }

    public NumberControl minValue(int minValue) {
        this.minValue = minValue;
        return this;
    }

    public NumberControl maxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public NumberControl changeAmount(int changeAmount) {
        this.changeAmount = changeAmount;
        return this;
    }

    public NumberControl fineChangeAmount(int fineChangeAmount) {
        this.fineChangeAmount = fineChangeAmount;
        return this;
    }

    public NumberControl fastChangeAmount(int fastChangeAmount) {
        this.fastChangeAmount = fastChangeAmount;
        return this;
    }

    public NumberControl unit(Component unit) {
        this.unit = unit;
        this.invalidateTree();
        return this;
    }

    public NumberControl onValueChanged(Consumer<Integer> onValueChanged) {
        this.onValueChanged = onValueChanged;
        return this;
    }

    public NumberControl height(int height) {
        this.height = height;
        this.invalidateTree();
        return this;
    }

    public NumberControl allowInput(boolean allowInput) {
        this.allowInput = allowInput;
        this.invalidateTree();
        return this;
    }

    @Override
    protected void build() {
        this
            .horizontal()
            .height(height)
            .growWidth()
            .children(
                // Scale down
                allowInput
                    ? button("-")
                    .style(buttonStyle())
                    .size(height)
                    .onClick(event -> {
                        value.update(v -> updateAmount(v, -changeAmount()));
                        onValueChanged.accept(value.get());
                    })
                    : null,
                box()
                    .grow()
                    .alignCenter()
                    .height(height)
                    .style(style()
                        .backgroundColor(UiColor.BLACK_A30))
                    .padding(1, 0, 0, 0)
                    .horizontal()
                    .children(
                        text(String.valueOf(value.get()))
                            .style(style()
                                .fontScale(0.75f)),
                        text(unit)
                            .style(style()
                                .fontScale(0.6f)
                                .textColor(UiColor.GRAY))
                    ),
                // Scale up
                allowInput
                    ? button("+")
                    .style(buttonStyle())
                    .size(height)
                    .onClick(event -> {
                        value.update(v -> updateAmount(v, changeAmount()));
                        onValueChanged.accept(value.get());
                    })
                    : null
            );
    }

    private Style buttonStyle() {
        return style()
            .when(c -> Screen.hasShiftDown() && fineChangeAmount != 0, style()
                .overlayColor(UiColor.TEAL.alpha(0.4f)))
            .when(c -> Screen.hasShiftDown() && fastChangeAmount != 0, style()
                .overlayColor(UiColor.LIME.alpha(0.4f)));
    }

    private int updateAmount(int currentValue, int delta) {
        int newValue = currentValue + delta;
        if (newValue < minValue) {
            return currentValue;
        } else if (newValue > maxValue) {
            return currentValue;
        }
        return newValue;
    }

    private int changeAmount() {
        if (Screen.hasShiftDown()) {
            if (fineChangeAmount != 0) {
                return fineChangeAmount;
            }
            if (fastChangeAmount != 0) {
                return fastChangeAmount;
            }
        }
        return changeAmount;
    }
}
