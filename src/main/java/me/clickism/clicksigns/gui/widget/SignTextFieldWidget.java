package me.clickism.clicksigns.gui.widget;

import me.clickism.clicksigns.sign.SignTextField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import static me.clickism.clicksigns.entity.renderer.RoadSignBlockEntityRenderer.TEXT_RENDER_SCALE;
import static me.clickism.clicksigns.gui.GuiConstants.PIXELS_PER_BLOCK;

@Environment(EnvType.CLIENT)
public class SignTextFieldWidget extends TextFieldWidget {

    private static final int UNEDITABLE_COLOR = 0xFF5555;
    private static final float TEXT_FIELD_HEIGHT = 4f;

    private final TextRenderer textRenderer;
    private final SignTextField signTextField;

    public SignTextFieldWidget(int anchorX, int anchorY, SignTextField signTextField,
                               float iconScale, TextRenderer textRenderer) {
        super(textRenderer,
                (int) (signTextField.maxWidth * iconScale),
                (int) (TEXT_FIELD_HEIGHT * iconScale * signTextField.scale),
                Text.empty());

        this.textRenderer = textRenderer;
        this.signTextField = signTextField;
        this.setChangedListener(this::onTextChange);
        // Convert pixel based coordinates to screen coordinates based on iconScale
        this.setX(anchorX + (int) (signTextField.guiPos.x * iconScale));
        this.setY(anchorY - (int) (signTextField.guiPos.y * iconScale) - this.height);
    }

    public void onTextChange(String str) {
        float width = getRenderWidth(str);
        if (width > signTextField.maxWidth) {
            this.setText(str.substring(0, str.length() - 1));
            this.setEditableColor(UNEDITABLE_COLOR);
        } else {
            this.setEditableColor(TextFieldWidget.DEFAULT_EDITABLE_COLOR);
        }
    }

    /**
     * Get the width of the text when rendered on a sign.
     * @param str the text to measure.
     * @return the rendered width of the text.
     */
    private float getRenderWidth(String str) {
        return textRenderer.getWidth(str) * PIXELS_PER_BLOCK * TEXT_RENDER_SCALE * signTextField.scale;
    }
}
