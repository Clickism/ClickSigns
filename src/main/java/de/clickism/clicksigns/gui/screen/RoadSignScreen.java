package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.layout.LinearLayout;
import de.clickism.clicksigns.gui.widget.ElementProvider;
import de.clickism.clicksigns.gui.widget.SymbolElementWidget;
import de.clickism.clicksigns.gui.widget.TextElementWidget;
import de.clickism.clicksigns.gui.widget.TextureWidget;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.render.RoadSignRenderer;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.registry.TileSetRegistry;
import de.clickism.clicksigns.sign.texture.TiledTexture;
import de.clickism.clicksigns.sign.texture.TiledTextureGenerator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Road sign screen
 */
public class RoadSignScreen extends BaseScreen {
    private static final int PADDING = 8;

    private final BlockPos blockPos;
    private RoadSign roadSign;

    private final List<ElementProvider> elementProviders = new ArrayList<>();

    /**
     * Creates a new road sign screen.
     */
    public RoadSignScreen(@Nullable Screen parent, RoadSignBlockEntity entity) {
        super(parent);
        this.blockPos = entity.getBlockPos();
        var roadSign = entity.roadSign();
        if (roadSign == null) {
            roadSign = RoadSignRenderer.defaultRoadSign();
        }
        this.roadSign = roadSign;
    }

    @Override
    protected void init() {
        var halfWidth = width / 2;
        var halfHeight = height / 2;

        // Add road sign texture
        var textureWidget = new TextureWidget(halfWidth, halfHeight, roadSign.texture());
        textureWidget.center();
        this.addRenderableWidget(textureWidget);

        // Add confirm button
        var confirmButton = confirmButton();
        this.addRenderableWidget(confirmButton);

        // Add change tileset button
        var changeTileSetButton = changeTileSetButton();
        this.addRenderableWidget(changeTileSetButton);

        // Layout
        LinearLayout.vertical()
                .center()
                .padding(PADDING)
                .add(textureWidget)
                .add(confirmButton)
                .add(changeTileSetButton)
                // Layout from center
                .layout(halfWidth, halfHeight);

        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        this.elementProviders.clear();
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = new SymbolElementWidget(anchorX, anchorY, symbol, this);
                this.elementProviders.add(symbolWidget);
                this.addRenderableWidget(symbolWidget);
            } else if (element instanceof TextElement textElement) {
                var textBox = new TextElementWidget(anchorX, anchorY, textElement, roadSign.colorResolver());
                this.elementProviders.add(textBox);
                this.addRenderableWidget(textBox);
            }
        }
    }

    private Button confirmButton() {
        return Button.builder(Component.translatable("clicksigns.text.confirm"), button -> {
                    var roadSign = readRoadSign();
                    Platform.network().sendToServer(new RoadSignUpdatePacket(blockPos, roadSign));
                    this.onClose();
                })
                .build();
    }

    private Button changeTemplateButton() {
        return Button.builder(Component.translatable("clicksigns.text.change_template"), button -> {
                    // TODO
                })
                .build();
    }

    // TODO: Make proper screen for selecting templates and tilesets
    private Button changeTileSetButton() {
        // TODO: Translate
        return Button.builder(Component.literal("Change Tileset"), button -> {
                    var currentTexture = roadSign.texture();
                    if (currentTexture instanceof TiledTexture tiledTexture) {
                        var tileSetId = tiledTexture.tileSet();
                        var allTileSetIds = TileSetRegistry.allIds();
                        var currentIndex = allTileSetIds.indexOf(tileSetId);
                        var nextIndex = (currentIndex + 1) % allTileSetIds.size();
                        var nextTileSetId = allTileSetIds.get(nextIndex);
                        var nextTileSet = TileSetRegistry.get(nextTileSetId);
                        if (nextTileSet != null) {
                            this.roadSign = this.roadSign.withTexture(
                                    TiledTextureGenerator.generate(nextTileSet, tiledTexture.blockWidth(), tiledTexture.blockHeight()));
                            this.rebuildWidgets();
                        }
                    }
                })
                .build();
    }

    private RoadSign readRoadSign() {
        var elements = elementProviders.stream().map(ElementProvider::element).toList();
        return new RoadSign(roadSign.texture(), roadSign.backTexture(), elements);
    }

    @Override
    protected void rebuildWidgets() {
        // Save current road sign data
        this.roadSign = readRoadSign();
        super.rebuildWidgets();
    }
}
