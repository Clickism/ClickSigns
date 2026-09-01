package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.edit.SignEditScreen;
import de.clickism.clicksigns.gui.screen.template.TemplateMenuScreen;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.generator.TextureTiler;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.elements.AlignmentSelector;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.UiScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * Road sign overview screen.
 * <p>
 * Provides an easy way to edit the texts of a road sign, its alignment,
 * change its template, and open the editor.
 */
public class SignOverviewScreen extends UiScreen<SignOverviewScreen> {

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
                        // TODO: No hover style for plate?
                        uiElement.style(style()
                            .whenHovered(style()
                                .borderColor(UiColor.RED)));
                        // Element specific config
                        var signElement = editableSignElement.current();
                        if (signElement instanceof TextElement) {
                            uiElement.tooltip(t("clicksigns.overview.text.tooltip"));
                        } else if (signElement instanceof SymbolElement symbol) {
                            uiElement
                                .tooltip(t("clicksigns.overview.symbol.tooltip"))
                                .onClick(event -> {
                                    event.playSound();
                                    // TODO: Refactor
                                    // Left click
                                    if (GuiUtils.isLeftClick(event.button())) {
                                        // Cycle to next symbol in the same category
                                        var nextSymbol = symbol.symbol().nextInCategory();
                                        roadSign.updateElement(
                                            editableSignElement.id(),
                                            element -> ((SymbolElement) element).withSymbol(nextSymbol)
                                        );
                                    }
                                    // Right click
                                    if (GuiUtils.isRightClick(event.button())) {
                                        // Open symbol menu
                                        var built = roadSign.build();
                                        var colorResolver = built.colorResolver();
                                        var entries = SignRegistries.SYMBOLS.all().stream()
                                            .map(s -> new de.clickism.clicksigns.ui.TextureList.Entry(
                                                s.texture().resolve(colorResolver),
                                                s.identifier(),
                                                // TODO: Handle uncategorized symbols
                                                s.resolveCategory()
                                            ))
                                            .toList();

                                        // Find sign background primary color
                                        var backgroundColor = UiColor.BLACK_A50;
                                        if (built.frontSource() instanceof TiledTextureSource tiled) {
                                            var primary = tiled.primaryColor();
                                            if (primary != null) {
                                                backgroundColor = UiColor.rgba(primary);
                                            }
                                        }

                                        new TextureSelectScreen(l("Select Symbol"), entries, backgroundColor)
                                            .onTextureSelected(entry -> {
                                                var newSymbol = SignRegistries.SYMBOLS.get(entry.identifier());
                                                if (newSymbol == null) return;
                                                roadSign.updateElement(
                                                    editableSignElement.id(),
                                                    element -> ((SymbolElement) element).withSymbol(newSymbol)
                                                );
                                            }).open();
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
                                    .onClick(event -> {
                                        new TemplateSelectScreen()
                                            .onTemplateSelected(template -> {
                                                // Change template
                                                roadSign.loadSign(template.build());
                                            })
                                            .open();
                                    }),
                                button("New Edit Screen")
                                    .buttonColor(UiColor.BEIGE)
                                    .growWidth()
                                    .onClick(e -> {
                                        new de.clickism.clicksigns.ui.SignEditScreen(roadSign.build())
                                            .onSignUpdate(roadSign::loadSign)
                                            .open();
                                    }),
                                // Edit button
                                button(t("✎", "clicksigns.text.edit"))
                                    .growWidth()
                                    .onClick(event -> {
                                        // Open Editor
                                        GuiUtils.openScreen(new SignEditScreen(roadSign.build(), roadSign::loadSign, UiScreenHandler.current()));
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
                                    .onAlignmentChange(roadSign::alignment)
                            )
                    )
            );
    }
}
