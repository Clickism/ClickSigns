package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.layout.LinearLayout;
import de.clickism.clicksigns.gui.widget.SymbolWidget;
import de.clickism.clicksigns.gui.widget.TextElementWidget;
import de.clickism.clicksigns.gui.widget.TextureWidget;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Road sign screen
 */
public class RoadSignScreen extends ScreenWithBackground {
    private static final int PADDING = 8;

    private final RoadSignBlockEntity entity;

    /**
     * Creates a new road sign screen.
     */
    public RoadSignScreen(RoadSignBlockEntity entity) {
        super(Component.translatable("clicksigns.text.road_sign_edit_screen"));
        this.entity = entity;
    }

    @Override
    protected void init() {
        var roadSign = entity.roadSign();

        var halfWidth = width / 2;
        var halfHeight = height / 2;

        // Add road sign texture
        var textureWidget = new TextureWidget(halfWidth, halfHeight, entity.roadSign().texture());
        textureWidget.center();
        this.addRenderableWidget(textureWidget);


        // Add confirm button
        var confirmButton = confirmButton();
        this.addRenderableWidget(confirmButton);

        // Layout
        LinearLayout.vertical()
                .center()
                .padding(PADDING)
                .add(textureWidget)
                .add(confirmButton)
                // Layout from center
                .layout(halfWidth, halfHeight);

        // Add symbol elements
        int anchorX = textureWidget.getX();
        int anchorY = textureWidget.getY() + textureWidget.getHeight();
        for (var element : roadSign.elements()) {
            if (!(element instanceof SymbolElement symbol)) continue;
            var symbolWidget = new SymbolWidget(anchorX, anchorY, symbol);
            this.addRenderableWidget(symbolWidget);
        }

        // Add elements
        for (var element : roadSign.elements()) {
            if (!(element instanceof TextElement textElement)) continue;
            var textBox = new TextElementWidget(anchorX, anchorY, textElement);
            this.addRenderableWidget(textBox);
        }
    }

    protected Button confirmButton() {
        return Button.builder(Component.translatable("clicksigns.text.confirm"), button -> {
                    // TODO: Implement
                    this.onClose();
                })
                .build();
    }
}
