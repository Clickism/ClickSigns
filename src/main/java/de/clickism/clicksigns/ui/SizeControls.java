package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.util.Size;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.reactivity.State;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;

public class SizeControls extends UiComponent<SizeControls> implements Headers {
    private final State<Size> size;

    private Consumer<Size> onSizeChanged = size -> {};

    private @NotNull Size minSize = new Size(1, 1);
    private @NotNull Size maxSize = new Size(Integer.MAX_VALUE, Integer.MAX_VALUE);

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
        var fieldHeight = 14;
        return box()
            .growWidth()
            .alignCenter()
            .childGap(4)
            .children(
                smallHeader(isWidth
                    ? l("Width")
                    : l("Height"))
                    .padding(0),
                box()
                    .horizontal()
                    .growWidth()
                    .children(
                        // Scale down
                        button("-")
                            // TODO: Translate
                            .tooltip(l("§7Tip: §rHold Shift for fine control"))
                            .size(fieldHeight)
                            .onClick(event -> {
                                changeSize(isWidth, -changeAmount());
                            }),
                        box()
                            .growWidth()
                            .height(fieldHeight)
                            .alignCenter()
                            .style(style()
                                .backgroundColor(UiColor.BLACK_A30))
                            .padding(1, 0, 0, 0)
                            .horizontal()
                            .children(
                                text(String.valueOf(isWidth
                                    ? size.get().width()
                                    : size.get().height()))
                                    .style(style()
                                        .fontScale(0.75f)),
                                text("px")
                                    .style(style()
                                        .fontScale(0.6f)
                                        .textColor(UiColor.GRAY))
                            ),
                        // Scale up
                        button("+")
                            .tooltip(l("§7Tip: §rHold Shift for fine control"))
                            .size(fieldHeight)
                            .onClick(event -> {
                                changeSize(isWidth, changeAmount());
                            })
                    )
            );
    }

    private void changeSize(boolean isWidth, int delta) {
        var current = size.get();
        var newSize = isWidth
            ? current.withWidth(current.width() + delta)
            : current.withHeight(current.height() + delta);
        if (!newSize.equals(newSize.clamped(minSize, maxSize))) {
            // If out of bounds, do not update
            return;
        }
        // Update the size state and notify the listener
        size.update(newSize);
        onSizeChanged.accept(newSize);
    }

    private int changeAmount() {
        return Screen.hasShiftDown()
            ? 1
            : 8;
    }
}
