package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.sign.Alignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.function.Consumer;

public class AlignmentWidget extends NestedWidget {
    private static final int BUTTON_SIZE = 20;
    private static final Map<Alignment, String> ALIGNMENT_ICONS = Map.of(
            Alignment.TOP_LEFT, "↖",
            Alignment.TOP_CENTER, "↑",
            Alignment.TOP_RIGHT, "↗",
            Alignment.CENTER_LEFT, "←",
            Alignment.CENTER, "•",
            Alignment.CENTER_RIGHT, "→",
            Alignment.BOTTOM_LEFT, "↙",
            Alignment.BOTTOM_CENTER, "↓",
            Alignment.BOTTOM_RIGHT, "↘"
    );

    private static final Map<Alignment, String> TEXT_ALIGNMENT_ICONS = Map.of(
            Alignment.TEXT_LEFT, "←",
            Alignment.TEXT_CENTER, "•",
            Alignment.TEXT_RIGHT, "→"
    );

    private static final Alignment[] TEXT_ALIGNMENTS = {
            Alignment.TEXT_LEFT,
            Alignment.TEXT_CENTER,
            Alignment.TEXT_RIGHT
    };

    protected AlignmentWidget(int x, int y, Alignment currentAlignment, Consumer<Alignment> onAlignmentSelected, boolean text) {
        super(x, y);
        // Add buttons
        var padding = 8;
        var alignments = text ? TEXT_ALIGNMENTS : Alignment.values();
        for (Alignment alignment : alignments) {
            int buttonX = x + (int) ((alignment.offset().x + 1) * (BUTTON_SIZE + padding));
            int buttonY = y + (int) ((-alignment.offset().y + 1) * (BUTTON_SIZE + padding));
            buttonY = text ? y : buttonY; // For text alignment, keep button in single row
            var icon = text ? TEXT_ALIGNMENT_ICONS.get(alignment) : ALIGNMENT_ICONS.get(alignment);
            var button = Button.builder(Component.literal(icon), b -> {
                        onAlignmentSelected.accept(alignment);
                    })
                    .bounds(buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE)
                    .build();
            if (alignment != currentAlignment) {
                button.setAlpha(GuiUtils.INACTIVE_ALPHA);
            }
            addChildAndUpdate(button);
        }
    }

    public static AlignmentWidget allAlignments(int x, int y, Alignment currentAlignment, Consumer<Alignment> onAlignmentSelected) {
        return new AlignmentWidget(x, y, currentAlignment, onAlignmentSelected, false);
    }

    public static AlignmentWidget textAlignments(int x, int y, Alignment currentAlignment, Consumer<Alignment> onAlignmentSelected) {
        return new AlignmentWidget(x, y, currentAlignment, onAlignmentSelected, true);
    }
}
