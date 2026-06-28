package de.clickism.clicksigns.gui.screen.overview;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.screen.edit.SignEditScreen;
import de.clickism.clicksigns.gui.screen.overview.widget.OverviewSymbolWidget;
import de.clickism.clicksigns.gui.screen.overview.widget.OverviewTextWidget;
import de.clickism.clicksigns.gui.screen.template.TemplateMenuScreen;
import de.clickism.clicksigns.gui.util.ElementProvider;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.AlignmentWidget;
import de.clickism.clicksigns.gui.widget.CategoryHeaderWidget;
import de.clickism.clicksigns.gui.widget.SignWidget;
import de.clickism.clicksigns.network.RoadSignUpdatePacket;
import de.clickism.clicksigns.platform.Platform;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

/**
 * Road sign screen
 */
public class SignOverviewScreen extends BaseScreen {
    private static final int PADDING = 8;

    private final BlockPos blockPos;
    private RoadSign roadSign;
    private SignWidget signWidget;

    /**
     * Creates a new road sign screen.
     */
    public SignOverviewScreen(@Nullable Screen parent, RoadSignBlockEntity entity) {
        super(parent);
        this.supportCollidingWidgets = true;
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
        this.signWidget = new SignWidget(0, 0, roadSign, OverviewTextWidget::new, OverviewSymbolWidget::new, this);
        this.addRenderableWidget(signWidget);

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
                .add(signWidget)
                .add(LinearLayout.spacer(10))
                .add(confirmButton)
                .add(templateButton)
                .add(editButton)
                // Layout from center
                .layout(halfWidth, halfHeight);

        var alignmentX = confirmButton.getX() + confirmButton.getWidth() + 10;
        var alignmentWidget = AlignmentWidget.allAlignments(alignmentX, confirmButton.getY(), roadSign.alignment(), alignment -> {
            this.roadSign = roadSign.withAlignment(alignment);
            this.rebuildWidgets();
        });
        this.addRenderableWidget(alignmentWidget);

        var alignmentHeader = new CategoryHeaderWidget(alignmentWidget.getWidth(), Component.translatable("clicksigns.text.alignment"));
        alignmentHeader.setX(alignmentWidget.getX());
        alignmentHeader.setY(alignmentWidget.getY() - alignmentHeader.getHeight());
        this.addRenderableWidget(alignmentHeader);
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
            GuiUtils.openScreen(new SignEditScreen(roadSign, sign -> {
                this.roadSign = sign;
                this.rebuildWithoutReading();
            }, this));
        }).build();
    }

    private Button changeTemplateButton() {
        var title = Component.literal("📝 ")
                .append(Component.translatable("clicksigns.text.change_template"));
        return Button.builder(title, button -> {
                    GuiUtils.openScreen(new TemplateMenuScreen(this, (template) -> {
                        // Change template
                        this.roadSign = template.buildDefault();
                        this.rebuildWithoutReading();
                    }));
                })
                .build();
    }

    private RoadSign readRoadSign() {
        var elements = this.signWidget.elementProviders().stream()
                .map(ElementProvider::element)
                .toList();
        return roadSign.withElements(elements);
    }

    private void rebuildWithoutReading() {
        this.clearWidgets();
        this.init();
    }

    @Override
    protected void rebuildWidgets() {
        // Save current road sign data
        this.roadSign = readRoadSign();
        super.rebuildWidgets();
    }
}
