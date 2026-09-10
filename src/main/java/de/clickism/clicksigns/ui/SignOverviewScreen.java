package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clicksigns.ui.elements.SymbolView;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import net.minecraft.core.BlockPos;

import static de.clickism.clicksigns.ui.UiConstants.TEXTURE_RENDER_SCALE;
import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;
import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

/**
 * Road sign overview screen.
 * <p>
 * Provides an easy way to edit the texts of a road sign, its alignment,
 * change its template, and open the editor.
 */
public class SignOverviewScreen extends UiScreen<SignOverviewScreen> implements FancyHeaders {
    public static final int PANEL_HEIGHT = 92;

    private final BlockPos blockPos;
    private final EditableRoadSign roadSign;

    /**
     * Creates a new road sign overview screen for the given block entity.
     *
     * @param entity the road sign block entity to edit
     */
    public SignOverviewScreen(RoadSignBlockEntity entity) {
        this.blockPos = entity.getBlockPos();
        // Use entity road sign or default if null
        var roadSign = entity.roadSign();
        if (roadSign == null) {
            roadSign = RoadSign.DEFAULT;
        }
        this.roadSign = new EditableRoadSign(roadSign);
    }

    @Override
    public void build() {
        Ref<SignView> signViewRef = ref();

        this.alignCenter()
            .childGap(8)
            .grow()
            .children(
                // Sign view
                new SignView(roadSign)
                    .ref(signViewRef)
                    // Set up element logic
                    .elementConfig((uiElement, editableSignElement) -> {
                        var signElement = editableSignElement.current();
                        if (!(signElement instanceof PlateElement)) {
                            // No hover style for plate
                            uiElement.style(style()
                                .whenHovered(style()
                                    .borderColor(UiColor.RED)));
                        }
                        // Element specific config
                        if (signElement instanceof TextElement) {
                            uiElement.tooltip(t("clicksigns.overview.text.tooltip"));
                        } else if (signElement instanceof SymbolElement) {
                            uiElement
                                .tooltip(t("clicksigns.overview.symbol.tooltip"))
                                .onClick(event -> {
                                    event.playSound();
                                    SymbolView.handleSymbolChange(roadSign, editableSignElement, event);
                                });
                        }
                    }),

                // Container
                box()
                    .horizontal()
                    .childGap(8)
                    .growWidth()
                    .children(
                        // Spacer
                        box().growWidth(),

                        // Button container
                        box()
                            .alignCenter()
                            .childGap(4)
                            .height(PANEL_HEIGHT)
                            .width((int) (2 * BLOCK_PIXELS * TEXTURE_RENDER_SCALE)) // 2 Blocks
                            .padding(4)
                            .style(style()
                                .backgroundColor(UiColor.BLACK_A50))
                            .children(
                                // Spacer
                                // TODO: Better name, translate
                                smallHeader(l("Sign Options")).padding(0),
                                // Buttons
                                // Confirm button
                                button(t("✔", "clicksigns.text.confirm"))
                                    .growWidth()
                                    .buttonColor(UiColor.LIME)
                                    .onClick(event -> {
                                        // Send packet
                                        Platform.network().sendToServer(
                                            new RoadSignUpdatePacket(blockPos, roadSign.build())
                                        );
                                        // Close screen
                                        this.close();
                                    }),
                                // Template button
                                button(t("📝", "clicksigns.text.change_template"))
                                    .growWidth()
                                    .buttonColor(UiColor.ORANGE)
                                    .onClick(event -> {
                                        new TemplateSelectScreen()
                                            .onTemplateSelected(template -> {
                                                // Change template
                                                roadSign.copyFrom(template.build());
                                            })
                                            .open();
                                    }),
                                // Edit button
                                button(t("✎", "clicksigns.text.edit"))
                                    .buttonColor(UiColor.TEAL)
                                    .growWidth()
                                    .onClick(e -> {
                                        new SignEditScreen(roadSign.build())
                                            .onSignUpdate(roadSign::copyFrom)
                                            .open();
                                    })
                            ),

                        // Alignment
                        box()
                            .growWidth()
                            .children(
                                box()
                                    .padding(4)
                                    .childGap(4)
                                    .style(style()
                                        .backgroundColor(UiColor.BLACK_A50))
                                    .children(
                                        // Header
                                        smallHeader(t("clicksigns.text.alignment")).padding(0),
                                        // Selector
                                        new AlignmentSelector()
                                            .alignment(roadSign.alignment())
                                            .onAlignmentChange(roadSign::alignment)
                                    )
                            )
                    )
            );
    }
}
