package me.clickism.clicksigns.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;

import static me.clickism.clicksigns.gui.GuiConstants.BUTTON_HEIGHT;

@Environment(EnvType.CLIENT)
public class TextureChangerWidget extends ClickableWidget {

    private static final int PADDING = 3;

    private final TextureIndexDisplay indexDisplay;
    private final TextureChangeButtonWidget nextButton;
    private final TextureChangeButtonWidget prevButton;

    private final int textureCount;

    public TextureChangerWidget(TextRenderer textRenderer, int textureCount, BiConsumer<ButtonWidget, Integer> onIncrement) {
        super(0, 0, BUTTON_HEIGHT * 4 + PADDING * 2, BUTTON_HEIGHT, Text.empty());
        this.textureCount = textureCount;
        this.indexDisplay = new TextureIndexDisplay(textRenderer, BUTTON_HEIGHT * 2, BUTTON_HEIGHT, textureCount);
        this.nextButton = new TextureChangeButtonWidget(BUTTON_HEIGHT, Text.literal(">"),
                (button) -> onIncrement.accept(button, 1));
        this.prevButton = new TextureChangeButtonWidget(BUTTON_HEIGHT, Text.literal("<"),
                (button) -> onIncrement.accept(button, -1));
    }

    public void setTextureIndex(int textureIndex) {
        this.indexDisplay.setTextureIndex(textureIndex);
        if (textureIndex == 1) {
            this.prevButton.setAlpha(.3f);
        } else {
            this.prevButton.setAlpha(1f);
        }
        if (textureIndex == textureCount) {
            this.nextButton.setAlpha(.3f);
        } else {
            this.nextButton.setAlpha(1f);
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        prevButton.setX(x);
        indexDisplay.setX(x + prevButton.getWidth() + PADDING);
        nextButton.setX(indexDisplay.getX() + indexDisplay.getWidth() + PADDING);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        prevButton.setY(y);
        indexDisplay.setY(y);
        nextButton.setY(y);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        indexDisplay.render(context, mouseX, mouseY, delta);
        nextButton.render(context, mouseX, mouseY, delta);
        prevButton.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        indexDisplay.appendNarrations(builder);
        nextButton.appendNarrations(builder);
        prevButton.appendNarrations(builder);
    }


    @Override
    public void onClick(double mouseX, double mouseY) {
        if (prevButton.isMouseOver(mouseX, mouseY)) {
            prevButton.onClick(mouseX, mouseY);
        } else if (nextButton.isMouseOver(mouseX, mouseY)) {
            nextButton.onClick(mouseX, mouseY);
        } else {
            super.onClick(mouseX, mouseY);
        }
    }
}
