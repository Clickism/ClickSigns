package de.clickism.clicksigns.gui.widget;

import de.clickism.clicksigns.sign.Alignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.Map;
import java.util.function.Consumer;

public class AlignmentWidget extends NestedWidget {
    private static float NOT_SELECTED_ALPHA = .3f;
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

    public AlignmentWidget(int x, int y, Alignment currentAlignment, Consumer<Alignment> onAlignmentSelected) {
        super(x, y);
        // Add buttons
        var padding = 8;
        for (Alignment alignment : Alignment.values()) {
            int buttonX = x + (int) ((alignment.offset().x + 1) * (BUTTON_SIZE + padding));
            int buttonY = y + (int) ((-alignment.offset().y + 1) * (BUTTON_SIZE + padding));
            var button = Button.builder(Component.literal(ALIGNMENT_ICONS.get(alignment)), b -> {
                        onAlignmentSelected.accept(alignment);
                    })
                    .bounds(buttonX, buttonY, BUTTON_SIZE, BUTTON_SIZE)
                    .build();
            if (alignment != currentAlignment) {
                button.setAlpha(NOT_SELECTED_ALPHA);
            }
            addChild(button);
        }
    }
}
