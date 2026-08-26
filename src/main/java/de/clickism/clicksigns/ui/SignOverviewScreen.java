package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.TextureMenuScreen;
import de.clickism.clicksigns.gui.screen.edit.SignEditScreen;
import de.clickism.clicksigns.gui.screen.template.TemplateMenuScreen;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.gui.widget.TextureList;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.Element;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

import java.awt.*;
import java.util.function.Consumer;

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

        Consumer<RoadSign> updateSign = newSign -> {
            this.roadSign = newSign;
            signViewRef.get().roadSign(newSign);
            alignmentSelectorRef.get().alignment(newSign.alignment());
        };

        return box()
            .alignCenter()
            .childGap(8)
            .grow()
            .children(
                // Sign view
                new SignView()
                    .roadSign(roadSign)
                    .ref(signViewRef)
                    // Set up element logic
                    .elementConfig((uiElement, signElement) -> {
                        // TODO: No hover style for plate?
                        uiElement.style(s -> s
                            .whenHovered(h -> h
                                .border(Color.RED)));
                        // Element specific config
                        if (signElement instanceof TextElement) {
                            uiElement.tooltip(t("clicksigns.overview.text.tooltip"));
                        } else if (signElement instanceof SymbolElement symbol) {
                            uiElement
                                .tooltip(t("clicksigns.overview.symbol.tooltip"))
                                .onClick(event -> {
                                    // TODO: Refactor
                                    if (GuiUtils.isLeftClick(event.button())) {
                                        // Cycle to next symbol in the same category
                                        var nextSymbol = symbol.symbol().nextInCategory();
                                        var newSign = roadSign.replaceElement(symbol, symbol.withSymbol(nextSymbol));
                                        updateSign.accept(newSign);
                                    }
                                    // Right click
                                    if (GuiUtils.isRightClick(event.button())) {
                                        // Open symbol menu
                                        // TODO: Add uncategorized symbols at the end
                                        var categoryToTextures = SignRegistries.SYMBOLS.categoryToEntriesAndThen(s -> new TextureList.IdentifiableTexture(
                                            s.identifier(),
                                            s.texture().resolve(roadSign.colorResolver())));
                                        // Open symbol selector screen
                                        var screen = new TextureMenuScreen<>(this, categoryToTextures, identifier -> {
                                            var s = SignRegistries.SYMBOLS.get(identifier);
                                            if (s == null) return;
                                            var newSign = roadSign.replaceElement(symbol, symbol.withSymbol(s));
                                            updateSign.accept(newSign);
                                            GuiUtils.closeScreen();
                                        });
                                        GuiUtils.openScreen(screen);
                                    }
                                });
                        }
                    }),

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
                                // Confirm button
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
                                // Template button
                                button(t("📝", "clicksigns.text.change_template"))
                                    .growWidth()
                                    .onClick(event -> {
                                        GuiUtils.openScreen(new TemplateMenuScreen(this, (template) -> {
                                            // Change template
                                            updateSign.accept(template.build());
                                        }));
                                    }),
                                // Edit button
                                button(t("✎", "clicksigns.text.edit"))
                                    .growWidth()
                                    .onClick(event -> {
                                        // Open Editor
                                        GuiUtils.openScreen(new SignEditScreen(roadSign, updateSign, this));
                                    })
                            ),

                        // Alignment
                        box()
                            .children(
                                // Header
                                text(t("clicksigns.text.alignment").copy()
                                    .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GRAY))
                                    .height(16) // Also use as spacer
                                    .growWidth()
                                    .alignTextCenter(),
                                // Selector
                                new AlignmentSelector()
                                    .alignment(roadSign.alignment())
                                    .ref(alignmentSelectorRef)
                            )

                    )
            );
    }
}
