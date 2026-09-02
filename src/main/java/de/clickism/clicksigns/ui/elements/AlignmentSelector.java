package de.clickism.clicksigns.ui.elements;

import com.mojang.math.Axis;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.reactivity.State;

import java.util.List;
import java.util.function.Consumer;

public class AlignmentSelector extends UiComponent<AlignmentSelector> {
    private static final int BUTTON_SIZE = 20;
    private static final int CHILD_GAP = 8;
    public static final int TOTAL_SIZE = BUTTON_SIZE * 3 + CHILD_GAP * 2;

    private static final String DIRECTIONAL_ICON = "→";
    private static final String CENTER_ICON = "•";

    private boolean textOnly = false;

    private final State<Alignment> alignment = state(Alignment.TOP_RIGHT);
    private Consumer<Alignment> onAlignmentChange = alignment -> {};

    public Alignment alignment() {
        return alignment.get();
    }

    public AlignmentSelector alignment(Alignment alignment) {
        this.alignment.update(alignment);
        return this;
    }

    public AlignmentSelector onAlignmentChange(Consumer<Alignment> listener) {
        this.onAlignmentChange = listener;
        return this;
    }

    public AlignmentSelector textOnly(boolean textOnly) {
        this.textOnly = textOnly;
        return this;
    }

    public List<Alignment> alignmentValues() {
        return textOnly
            ? List.of(Alignment.TOP_LEFT, Alignment.TOP_CENTER, Alignment.TOP_RIGHT)
            : List.of(Alignment.values());
    }

    @Override
    protected void build() {
        // TODO: Doesnt need to be reactive
        var currentAlignment = alignment.get();
        // Build the UI for the alignment selector here
        var grid = grid(3)
            .childGap(CHILD_GAP);
        add(grid);
        for (Alignment a : alignmentValues()) {
            var button = button("") // Leave empty as we custom render the icon
                .width(BUTTON_SIZE)
                .height(BUTTON_SIZE)
                .style(style()
                    .addPostRenderHook((context, el) -> {
                        // Calculat center of the button
                        var bounds = el.bounds();
                        var centerX = bounds.x() + bounds.width() / 2;
                        var centerY = bounds.y() + bounds.height() / 2;

                        // Get the angle of the alignment offset
                        var alignmentOffset = a.offset();
                        if (textOnly) {
                            alignmentOffset.y = 0; // Ignore vertical offset for text only mode
                        }
                        var degrees = (int) Math.toDegrees(Math.atan2(-alignmentOffset.y, alignmentOffset.x));

                        // Rotate the graphics context around the center of the button
                        var graphics = context.graphics();
                        graphics.pose().pushPose();
                        graphics.pose().rotateAround(Axis.ZP.rotationDegrees(degrees), centerX, centerY, 0);
                        // Draw the icon
                        var icon = (a == Alignment.CENTER || (textOnly && a == Alignment.TEXT_CENTER))
                            ? CENTER_ICON
                            : DIRECTIONAL_ICON;
                        // Draw the icon centered in the button
                        var font = context.font();
                        var iconWidth = font.width(icon);
                        var iconHeight = font.lineHeight;
                        graphics.drawString(
                            font,
                            icon,
                            centerX - iconWidth / 2,
                            centerY - iconHeight / 2,
                            UiColor.WHITE.color(),
                            false // No shadow since looksa bit weird when rotated
                        );
                        // Pop pose
                        graphics.pose().popPose();
                    })
                    .when(context -> !a.equals(currentAlignment), style()
                        .alpha(GuiUtils.INACTIVE_ALPHA)))
                .onClick(event -> {
                    alignment.update(a);
                    onAlignmentChange.accept(a);
                });
            grid.add(button);
        }
    }
}
