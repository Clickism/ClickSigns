package me.clickism.clicksigns.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TextureIndexDisplay extends ClickableWidget {
    private final TextFieldWidget textFieldWidget;
    private final EditableTextWidget textWidget;
    private final int textureCount;

    public TextureIndexDisplay(TextRenderer textRenderer, int width, int height, int textureCount) {
        super(0, 0, width, height, Text.empty());
        this.textFieldWidget = new TextFieldWidget(textRenderer, width, height, Text.empty());
        this.textWidget = new EditableTextWidget(width, height, Text.empty(), textRenderer);
        this.textureCount = textureCount;
        this.textWidget.alignCenter();
    }

    public void setTextureIndex(int textureIndex) {
        textWidget.setText(Text.literal(textureIndex + "/" + textureCount));
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        textWidget.setX(x);
        textFieldWidget.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        textWidget.setY(y + 1);
        textFieldWidget.setY(y);
    }

    @Override
    public int getWidth() {
        return Math.max(textFieldWidget.getWidth(), textWidget.getWidth());
    }

    @Override
    public int getHeight() {
        return Math.max(textFieldWidget.getHeight(), textWidget.getHeight());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        textFieldWidget.appendNarrations(builder);
        textWidget.appendNarrations(builder);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        textFieldWidget.render(context, mouseX, mouseY, delta);
        textWidget.render(context, mouseX, mouseY, delta);
    }


    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        consumer.accept(textFieldWidget);
        consumer.accept(textWidget);
    }
}
