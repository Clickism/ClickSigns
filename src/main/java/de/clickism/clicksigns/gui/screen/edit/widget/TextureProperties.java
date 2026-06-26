package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.sign.RoadSign;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TextureProperties extends NestedWidget {
    public TextureProperties(int x, int y, Screen parent, RoadSign roadSign, Consumer<RoadSign> onUpdate) {
        super(x, y);
        var frontWidget = new EditTextureWidget(parent, 0, 0, roadSign.frontSource(), roadSign.colorResolver(), source -> {
            onUpdate.accept(roadSign.withFront(source.resizeToFit(roadSign)));
        });
        addChild(frontWidget);
        var backWidget = new EditTextureWidget(parent, 0, 0, roadSign.backSource(), roadSign.colorResolver(), source -> {
            onUpdate.accept(roadSign.withBack(source.resizeToFit(roadSign)));
        });
        addChild(backWidget);
        LinearLayout.horizontal()
                .padding(8)
                .add(frontWidget)
                .add(backWidget)
                .layout(x, y);
        updateSize();
        int padding = 4;
        var frontLabel = new StringWidget(frontWidget.getX(), frontWidget.getY() + frontWidget.getHeight() + padding, frontWidget.getWidth(), 16, Component.literal("Front"), GuiUtils.font());
        addChild(frontLabel);
        var backLabel = new StringWidget(backWidget.getX(), backWidget.getY() + backWidget.getHeight() + padding, backWidget.getWidth(), 16, Component.literal("Back"), GuiUtils.font());
        addChild(backLabel);
        updateSize();
    }
}
