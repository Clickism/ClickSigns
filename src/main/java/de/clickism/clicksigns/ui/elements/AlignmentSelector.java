package de.clickism.clicksigns.ui.elements;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clickui.Component;
import de.clickism.clickui.reactivity.State;

import java.util.Map;

public class AlignmentSelector extends Component<AlignmentSelector> {
    private static final int BUTTON_SIZE = 20;
    private static final int CHILD_GAP = 8;
    public static final int TOTAL_SIZE = BUTTON_SIZE * 3 + CHILD_GAP * 2;

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

    private final State<Alignment> alignment = state(Alignment.TOP_RIGHT);

    public Alignment alignment() {
        return alignment.get();
    }

    public AlignmentSelector alignment(Alignment alignment) {
        this.alignment.update(alignment);
        return this;
    }

    @Override
    protected void build() {
        var currentAlignment = alignment.get();
        // Build the UI for the alignment selector here
        var grid = grid(3)
            .childGap(CHILD_GAP);
        add(grid);
        for (Alignment a : Alignment.values()) {
            var icon = ALIGNMENT_ICONS.get(a);
            var button = button(icon)
                .width(BUTTON_SIZE)
                .height(BUTTON_SIZE)
                .style(s -> s
                    .when(context -> !a.equals(currentAlignment), o -> o
                        .alpha(GuiUtils.INACTIVE_ALPHA)))
                .onClick(event -> alignment.update(a));
            grid.add(button);
        }
    }
}
