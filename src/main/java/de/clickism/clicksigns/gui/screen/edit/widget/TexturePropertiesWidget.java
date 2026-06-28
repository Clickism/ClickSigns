package de.clickism.clicksigns.gui.screen.edit.widget;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.sign.ColorResolver;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class TexturePropertiesWidget extends NestedWidget {
    public TexturePropertiesWidget(
            int x, int y,
            Screen parent,
            ColorResolver colorResolver,
            TextureSource frontSource,
            TextureSource backSource,
            OnTextureChanged onUpdate) {
        super(x, y);
        var frontWidget = new SelectTextureWidget(parent, 0, 0, frontSource, colorResolver, source -> {
            onUpdate.onTextureChanged(source, backSource);
        });
        addChild(frontWidget);
        var backWidget = new SelectTextureWidget(parent, 0, 0, backSource, colorResolver, source -> {
            onUpdate.onTextureChanged(frontSource, source);
        });
        addChild(backWidget);
        LinearLayout.horizontal()
                .padding(8)
                .add(frontWidget)
                .add(backWidget)
                .layout(x, y);
        updateSizeAndPosition();
        int padding = 4;
        var frontLabel = new StringWidget(frontWidget.getX(), frontWidget.getY() + frontWidget.getHeight() + padding, frontWidget.getWidth(), 16, Component.literal("Front"), GuiUtils.font());
        addChild(frontLabel);
        var backLabel = new StringWidget(backWidget.getX(), backWidget.getY() + backWidget.getHeight() + padding, backWidget.getWidth(), 16, Component.literal("Back"), GuiUtils.font());
        addChild(backLabel);
        updateSizeAndPosition();
    }

    /**
     * Interface for listening to texture changes.
     */
    public interface OnTextureChanged {
        void onTextureChanged(TextureSource frontSource, TextureSource backSource);
    }
}
