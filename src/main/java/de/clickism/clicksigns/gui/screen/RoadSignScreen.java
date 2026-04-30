package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.layout.LinearLayout;
import de.clickism.clicksigns.gui.widget.ElementProvider;
import de.clickism.clicksigns.gui.widget.SymbolElementWidget;
import de.clickism.clicksigns.gui.widget.TextElementWidget;
import de.clickism.clicksigns.gui.widget.TextureWidget;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static de.clickism.clicksigns.util.Constants.BLOCK_PIXELS;

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
            roadSign = RoadSign.DEFAULT;
        }
        this.roadSign = roadSign;
    }

    @Override
    protected void init() {
        var halfWidth = width / 2;
        var halfHeight = height / 2;

        // Add road sign texture
        var textureWidget = new TextureWidget(halfWidth, halfHeight, roadSign.frontTexture());
        textureWidget.center();
        this.addRenderableWidget(textureWidget);

        // Add confirm button
        var confirmButton = confirmButton();
        this.addRenderableWidget(confirmButton);

        // Add change tileset button
        var changeTileSetButton = changeTileSetButton();
        this.addRenderableWidget(changeTileSetButton);

        var sizeButton = new Button.Builder(Component.literal("Size"), button -> {
            if (roadSign.frontSource() instanceof TiledTextureSource src) {
                Texture tex = roadSign.frontTexture();
                this.roadSign = roadSign.withFront(new TiledTextureSource(src.tileSetId(), (int) (tex.width() + BLOCK_PIXELS), (int) (tex.height() + BLOCK_PIXELS)));
                this.rebuildWidgets();
            }
        }).build();
        this.addRenderableWidget(sizeButton);

        var alignmentButton = new Button.Builder(Component.literal("Alignment"), button -> {
            var currentAlignment = roadSign.alignment().ordinal();
            var nextAlignment = (currentAlignment + 1) % Alignment.values().length;
            this.roadSign = roadSign.withAlignment(Alignment.values()[nextAlignment]);
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Alignment: " + roadSign.alignment().name()), false);
            this.rebuildWidgets();
        }).build();
        this.addRenderableWidget(alignmentButton);

        // Layout
        LinearLayout.vertical()
                .center()
                .padding(PADDING)
                .add(textureWidget)
                .add(confirmButton)
                .add(changeTileSetButton)
                .add(sizeButton)
                .add(alignmentButton)
                // Layout from center
                .layout(halfWidth, halfHeight);

        // Calculate anchor for elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        // Add elements
        this.elementProviders.clear();
        for (var element : roadSign.elements()) {
            if (element instanceof SymbolElement symbol) {
                var symbolWidget = new SymbolElementWidget(anchorX, anchorY, symbol, roadSign.colorResolver(), this);
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
                    var front = roadSign.frontSource();
                    if (front instanceof TiledTextureSource tiled) {
                        var tileSetId = tiled.tileSetId();
                        var allTileSetIds = new ArrayList<>(SignRegistries.TILE_SETS.allIds());
                        var currentIndex = allTileSetIds.indexOf(tileSetId);
                        var nextIndex = (currentIndex + 1) % allTileSetIds.size();
                        var nextTileSetId = allTileSetIds.get(nextIndex);
                        var nextTileSet = SignRegistries.TILE_SETS.get(nextTileSetId);
                        if (nextTileSet != null) {
                            var newSource = new TiledTextureSource(nextTileSetId, tiled.width(), tiled.height());
                            this.roadSign = this.roadSign.withFront(newSource);
                            this.rebuildWidgets();
                        }
                    }
                })
                .build();
    }

    private RoadSign readRoadSign() {
        var elements = elementProviders.stream().map(ElementProvider::element).toList();
        return roadSign.withElements(elements);
    }

    @Override
    protected void rebuildWidgets() {
        // Save current road sign data
        this.roadSign = readRoadSign();
        super.rebuildWidgets();
    }
}
