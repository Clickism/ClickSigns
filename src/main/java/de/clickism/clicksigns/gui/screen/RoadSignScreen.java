package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.layout.LinearLayout;
import de.clickism.clicksigns.gui.widget.*;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

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

        // Add template button
        var templateButton = changeTemplateButton();
        this.addRenderableWidget(templateButton);

        var editButton = editButton();
        this.addRenderableWidget(editButton);

        // Layout
        LinearLayout.vertical()
                .center()
                .padding(PADDING)
                .add(textureWidget)
                .add(LinearLayout.spacer(0, 10))
                .add(confirmButton)
                .add(templateButton)
                .add(editButton)
                // Layout from center
                .layout(halfWidth, halfHeight);

        var alignmentX = confirmButton.getX() + confirmButton.getWidth() + 10;
        var alignmentWidget = new AlignmentWidget(alignmentX, confirmButton.getY(), roadSign.alignment(), alignment -> {
            this.roadSign = roadSign.withAlignment(alignment);
            this.rebuildWidgets();
        });
        this.addRenderableWidget(alignmentWidget);

        var alignmentHeader = new CategoryHeaderWidget(alignmentWidget.getWidth(), Component.translatable("clicksigns.text.alignment"));
        alignmentHeader.setX(alignmentWidget.getX());
        alignmentHeader.setY(alignmentWidget.getY() - alignmentHeader.getHeight());
        this.addRenderableWidget(alignmentHeader);

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
                var textBox = new TextElementWidget(anchorX, anchorY, textElement, roadSign.colorResolver(), roadSign.frontTexture().width());
                this.elementProviders.add(textBox);
                this.addRenderableWidget(textBox);
            }
        }
    }

    private Button confirmButton() {
        var title = Component.literal("✔ ")
                .append(Component.translatable("clicksigns.text.confirm"));
        return Button.builder(title, button -> {
                    var roadSign = readRoadSign();
                    Platform.network().sendToServer(new RoadSignUpdatePacket(blockPos, roadSign));
                    this.onClose();
                })
                .build();
    }

    private Button editButton() {
        var title = Component.literal("✎ ")
                .append(Component.translatable("clicksigns.text.edit"));
        return Button.builder(title, button -> {
            GuiUtils.openScreen(new RoadSignEditScreen(roadSign, this));
        }).build();
    }

    private Button changeTemplateButton() {
        var title = Component.literal("📝 ")
                .append(Component.translatable("clicksigns.text.change_template"));
        return Button.builder(title, button -> {
                    GuiUtils.openScreen(new TemplateMenuScreen(this, (template) -> {
                        // Change template
                        this.roadSign = template.buildDefault();
                        // Rebuild widgets
                        this.clearWidgets();
                        this.init();
                    }));
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
