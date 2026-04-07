package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.entity.RoadSignBlockEntity;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.util.ScreenWithBackground;
import de.clickism.clicksigns.gui.widget.TextElementBox;
import de.clickism.clicksigns.gui.widget.TextureWidget;
import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.element.TextElement;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * Road sign editor screen
 */
public class RoadSignEditScreen extends ScreenWithBackground {
    private static final int PADDING = 8;

    private final RoadSignBlockEntity entity;

    /**
     * Creates a new road sign editor screen.
     */
    public RoadSignEditScreen(RoadSignBlockEntity entity) {
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
        var layoutX = halfWidth;
        var layoutY = halfHeight;

        LinearLayout.vertical()
                .center()
                .padding(PADDING)
                .add(textureWidget)
                .add(confirmButton)
                .layout(layoutX, layoutY);

        // Add text element boxes
        for (RoadSignElement element : roadSign.elements()) {
            if (!(element instanceof TextElement textElement)) continue;
            var textBox = new TextElementBox(textElement, textureWidget.getX(), textureWidget.getY());
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
