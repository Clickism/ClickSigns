package de.clickism.clicksigns.ui.elements;

import com.mojang.math.Axis;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.ui.UiConstants;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.reactivity.State;
import de.clickism.clickui.style.Border;

import java.util.List;
import java.util.function.Consumer;

/**
 * A UI component that allows the user to select an alignment from a set of predefined options.
 */
public class AlignmentSelector extends UiComponent<AlignmentSelector> {
    private static final String DIRECTIONAL_ICON = "→";
    private static final String CENTER_ICON = "•";

    private final State<Alignment> alignment = state(Alignment.TOP_RIGHT);
    private boolean textOnly = false;
    private Consumer<Alignment> onAlignmentChange = alignment -> {};

    /**
     * Returns the current alignment.
     *
     * @return the current alignment
     */
    public Alignment alignment() {
        return alignment.get();
    }

    /**
     * Sets the current alignment and updates the UI.
     *
     * @param alignment the new alignment
     * @return this AlignmentSelector for method chaining
     */
    public AlignmentSelector alignment(Alignment alignment) {
        this.alignment.update(alignment);
        return this;
    }

    /**
     * Sets a listener that will be called whenever the alignment changes.
     *
     * @param listener the listener to call when the alignment changes
     * @return this AlignmentSelector for method chaining
     */
    public AlignmentSelector onAlignmentChange(Consumer<Alignment> listener) {
        this.onAlignmentChange = listener;
        return this;
    }

    /**
     * Sets whether the alignment selector should only show text alignments.
     *
     * @param textOnly true to only show text alignments, false to show all alignments
     * @return this AlignmentSelector for method chaining
     */
    public AlignmentSelector textOnly(boolean textOnly) {
        this.textOnly = textOnly;
        this.invalidateTree();
        return this;
    }

    /**
     * Returns the list of alignments to display based on the textOnly flag.
     *
     * @return the list of alignments to display
     */
    public List<Alignment> alignmentValues() {
        return textOnly
            ? List.of(Alignment.TEXT_LEFT, Alignment.TEXT_CENTER, Alignment.TEXT_RIGHT)
            : List.of(Alignment.values());
    }

    @Override
    protected void build() {
        var currentAlignment = alignment.get();
        // Build the UI for the alignment selector here
        var grid = grid(3)
            .childGap(4);
        add(grid);
        for (Alignment a : alignmentValues()) {
            var button = button("") // Leave empty as we custom render the icon
                .defaultBackground(false)
                .size(20)
                .style(style()
                    .backgroundColor(UiColor.BLACK)
                    .when(context -> a.equals(currentAlignment), style()
                        .borderPosition(Border.Position.INSIDE)
                        .borderColor(UiColor.WHITE))
                    .when(context -> !a.equals(currentAlignment), style()
                        .alpha(UiConstants.INACTIVE_ALPHA))
                    .addPostRenderHook((context, el) -> {
                        // Calculate center of the button
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
                    }))
                .onClick(event -> {
                    alignment.update(a);
                    onAlignmentChange.accept(a);
                });
            grid.add(button);
        }
    }
}
