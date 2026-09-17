package de.clickism.clicksigns.ui.components;

import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.State;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

import static de.clickism.clicksigns.util.ComponentUtil.l;

public class SizeControls extends UiComponent<SizeControls> implements CommonComponents {
    private final State<Size> size;

    private Consumer<Size> onSizeChanged = size -> {};

    private @NotNull Size minSize = new Size(1, 1);
    private @NotNull Size maxSize = new Size(Integer.MAX_VALUE, Integer.MAX_VALUE);

    private Component widthHeader = l("Width");
    private Component heightHeader = l("Height");
    private Component unit = l("px");

    private int changeAmount = 1;
    private int fineChangeAmount = 0;
    private boolean allowInput = true;

    public SizeControls(Size size) {
        this.size = state(size);
    }

    public SizeControls onSizeChanged(Consumer<Size> onSizeChanged) {
        this.onSizeChanged = onSizeChanged;
        return this;
    }

    public SizeControls minSize(Size minSize) {
        this.minSize = minSize;
        return this;
    }

    public SizeControls maxSize(Size maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public SizeControls widthHeader(Component widthHeader) {
        this.widthHeader = widthHeader;
        this.invalidateTree();
        return this;
    }

    public SizeControls heightHeader(Component heightHeader) {
        this.heightHeader = heightHeader;
        this.invalidateTree();
        return this;
    }

    public SizeControls changeAmount(int changeAmount) {
        this.changeAmount = changeAmount;
        return this;
    }

    public SizeControls fineChangeAmount(int fineChangeAmount) {
        this.fineChangeAmount = fineChangeAmount;
        return this;
    }

    public SizeControls unit(Component unit) {
        this.unit = unit;
        return this;
    }

    public SizeControls allowInput(boolean allowInput) {
        this.allowInput = allowInput;
        this.invalidateTree();
        return this;
    }

    @Override
    protected void build() {
        this.horizontal()
            .growWidth()
            .childGap(4)
            .children(
                sizeControl(true),
                sizeControl(false)
            );
    }

    private UiElement<?> sizeControl(boolean isWidth) {
        return box()
            .growWidth()
            .alignCenter()
            .childGap(4)
            .children(
                smallHeader(isWidth
                    ? widthHeader
                    : heightHeader)
                    .padding(0),
                new NumberControl()
                    .value(isWidth
                        ? size.get().width()
                        : size.get().height())
                    .minValue(isWidth
                        ? minSize.width()
                        : minSize.height())
                    .maxValue(isWidth
                        ? maxSize.width()
                        : maxSize.height())
                    .changeAmount(changeAmount)
                    .fineChangeAmount(fineChangeAmount)
                    .unit(unit)
                    .allowInput(allowInput)
                    .onValueChanged(value -> {
                        updateSize(size -> isWidth
                            ? size.withWidth(value)
                            : size.withHeight(value)
                        );
                    })
            );
    }

    private void updateSize(Function<Size, Size> updater) {
        var newSize = updater.apply(size.get());
        if (!newSize.equals(newSize.clamped(minSize, maxSize))) {
            // If out of bounds, do not update
            return;
        }
        // Update the size state and notify the listener
        size.update(newSize);
        onSizeChanged.accept(newSize);
    }

    private int changeAmount() {
        if (fineChangeAmount != 0 && Screen.hasShiftDown()) {
            return fineChangeAmount;
        }
        return changeAmount;
    }
}
