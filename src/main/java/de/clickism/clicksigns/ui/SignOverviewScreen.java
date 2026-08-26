package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.SignEditScreen;
import de.clickism.clicksigns.gui.screen.template.TemplateMenuScreen;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.Element;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * Road sign overview screen.
 * <p>
 * Provides an easy way to edit the texts of a road sign, its alignment,
 * change its template, and open the editor.
 */
public class SignOverviewScreen extends UiScreen {

    private final BlockPos blockPos;
    private RoadSign roadSign;

    /**
     * Creates a new road sign overview screen for the given block entity.
     *
     * @param entity the road sign block entity to edit
     */
    public SignOverviewScreen(RoadSignBlockEntity entity) {
        this.blockPos = entity.getBlockPos();
        this.roadSign = entity.roadSign();
        if (this.roadSign == null) {
            this.roadSign = RoadSign.DEFAULT;
        }
    }

    @Override
    public Element<?> build() {
        Ref<SignView> signViewRef = ref();
        Ref<AlignmentSelector> alignmentSelectorRef = ref();
        return box()
            .alignCenter()
            .childGap(8)
            .grow()
            .children(
                // Sign view
                new SignView()
                    .roadSign(roadSign)
                    .ref(signViewRef),

                // Container
                box()
                    .horizontal()
                    .childGap(16)
                    .children(
                        // Spacer
                        box().width(AlignmentSelector.TOTAL_SIZE),

                        // Button container
                        box()
                            .alignCenter()
                            .childGap(8)
                            .width(128)
                            .children(
                                // Spacer
                                box().height(8),
                                // Buttons
                                button(t("✔", "clicksigns.text.confirm"))
                                    .growWidth()
                                    .onClick(event -> {
                                        // Read and update sign
                                        var newElements = signViewRef.get().elementProviders().stream()
                                            .map(ElementProvider::element)
                                            .toList();
                                        var newAlignment = alignmentSelectorRef.get().alignment();
                                        var newSign = this.roadSign
                                            .withElements(newElements)
                                            .withAlignment(newAlignment);
                                        // Send packet
                                        Platform.network().sendToServer(new RoadSignUpdatePacket(blockPos, newSign));
                                        // Close screen
                                        this.back();
                                    }),
                                button(t("📝", "clicksigns.text.change_template"))
                                    .growWidth()
                                    .onClick(event -> {
                                        GuiUtils.openScreen(new TemplateMenuScreen(this, (template) -> {
                                            // Change template
                                            this.roadSign = template.build();
                                            signViewRef.get().roadSign(roadSign);
                                        }));
                                    }),
                                button(t("✎", "clicksigns.text.edit"))
                                    .growWidth()
                                    .onClick(event -> {
                                        // Open Editor
                                        GuiUtils.openScreen(new SignEditScreen(roadSign, sign -> {
                                            this.roadSign = sign;
                                            signViewRef.get().roadSign(sign);
                                        }, this));
                                    })
                            ),

                        // Alignment
                        box()
                            .children(
                                text(t("clicksigns.text.alignment").copy()
                                    .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GRAY))
                                    .height(16) // Also use as spacer
                                    .growWidth()
                                    .alignTextCenter()
                                    .style(s -> s
                                        .alpha(1f)),
                                new AlignmentSelector()
                                    .alignment(roadSign.alignment())
                                    .ref(alignmentSelectorRef)
                            )

                    )
            );
    }
}
