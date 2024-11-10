package me.clickism.clicksigns.gui;

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.Utils;
import me.clickism.clicksigns.entity.RoadSignBlockEntity;
import me.clickism.clicksigns.gui.widget.EditableTextWidget;
import me.clickism.clicksigns.gui.widget.SignTextFieldWidget;
import me.clickism.clicksigns.gui.widget.TextureChangerWidget;
import me.clickism.clicksigns.sign.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.IconWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.clickism.clicksigns.gui.GuiConstants.BUTTON_HEIGHT;
import static me.clickism.clicksigns.gui.GuiConstants.BUTTON_WIDTH;
import static me.clickism.clicksigns.gui.GuiConstants.NOT_SELECTED_ALPHA;
import static me.clickism.clicksigns.gui.GuiConstants.PIXELS_PER_BLOCK;

@Environment(EnvType.CLIENT)
public class RoadSignEditScreen extends Screen {

    private static final float ICON_SCALE = 3.5f;
    private static final int PADDING = 3;

    private static final String[] ALIGNMENT_ICONS = {"↖", "↑", "↗", "←", "•", "→", "↙", "↓", "↘"};

    private final RoadSignBlockEntity entity;
    private final RoadSignBuilder builder = new RoadSignBuilder();

    private List<SignTextFieldWidget> signTextFieldWidgets;
    private TextureChangerWidget textureChangerWidget;

    public RoadSignEditScreen(RoadSignBlockEntity entity) {
        super(Text.translatable("clicksigns.text.road_sign_editor"));
        this.entity = entity;
        RoadSign roadSign = entity.getRoadSign();
        if (roadSign != null) {
            builder.templateId(roadSign.templateId())
                    .texts(roadSign.getTexts())
                    .textureIndex(roadSign.getTextureIndex())
                    .alignment(roadSign.getAlignment());
        }
    }

    private void refresh() {
        builder.texts(readEditorTexts());
        clearAndInit();
    }

    public void setTemplate(@NotNull RoadSignTemplate template, int textureIndex) {
        builder.templateId(template.getId())
                .textureIndex(textureIndex);
        refresh();
    }

    @Override
    protected void init() {
        RoadSignTemplate template = RoadSignTemplateRegistration.getTemplateOrError(builder.templateId());
        Text templateName = Text.of("🪧 " + template.getFormattedName());
        if (template.equals(RoadSignTemplateRegistration.ERROR)) {
            templateName = Text.of("⚠ " + builder.templateId().toString());
            builder.templateId(ClickSigns.ERROR_TEMPLATE_ID);
        }
        int textureCount = template.getTextures().size();
        if (builder.textureIndex() >= textureCount) {
            builder.textureIndex(0);
        }
        IconWidget iconWidget = addDrawableChild(getIconWidget(template, builder.textureIndex()));
        // Center the icon widget
        iconWidget.setPosition(width / 2 - iconWidget.getWidth() / 2, height / 2 - iconWidget.getHeight());
        placeTextFields(template, iconWidget);
        textureChangerWidget = addTextureChangeButton(textureCount);
        textureChangerWidget.setTextureIndex(builder.textureIndex() + 1);
        int startingY = iconWidget.getY() + iconWidget.getHeight() + PADDING;
        AxisOrganizer organizer = AxisOrganizer.vertical(width / 2, startingY, true, PADDING)
                .organize(addEditableTextField(templateName))
                .organize(addConfirmButton())
                .organize(textureChangerWidget)
                .organize(addTemplateChangeButton())
                .organize(addAlignmentTextWidget());
        addAlignmentButtons(organizer.getLastY() + PADDING);
    }

    private EditableTextWidget addAlignmentTextWidget() {
        return addEditableTextField(Text.translatable("clicksigns.text.alignment",
                Utils.titleCase(builder.alignment().name().replace('_', ' '))));
    }

    private EditableTextWidget addEditableTextField(Text text) {
        EditableTextWidget widget = new EditableTextWidget(text, textRenderer);
        widget.alignCenter();
        widget.setWidth(width);
        return addDrawableChild(widget);
    }

    private TextureChangerWidget addTextureChangeButton(int textureCount) {
        return addDrawableChild(new TextureChangerWidget(textRenderer, textureCount, (button, increment) -> {
            int index = Math.clamp(builder.textureIndex() + increment, 0, textureCount - 1);
            builder.textureIndex(index);
            textureChangerWidget.setTextureIndex(index + 1);
            refresh();
        }));
    }

    private IconWidget getIconWidget(RoadSignTemplate template, int textureIndex) {
        int iconWidth = (int) (ICON_SCALE * template.getWidth() * PIXELS_PER_BLOCK);
        int iconHeight = (int) (ICON_SCALE * template.getHeight() * PIXELS_PER_BLOCK);
        Identifier texture = template.getTextureOrError(textureIndex).getFrontTexture();
        var widget = IconWidget.create(
                iconWidth, iconHeight, texture, iconWidth, iconHeight
        );
        widget.active = false;
        return widget;
    }

    private void placeTextFields(RoadSignTemplate template, IconWidget iconWidget) {
        List<SignTextField> signTextFields = template.getTextFields();
        List<String> texts = builder.texts();
        signTextFieldWidgets = new ArrayList<>(signTextFields.size());
        int anchorY = iconWidget.getY() + iconWidget.getHeight();
        for (int i = 0; i < signTextFields.size(); i++) {
            SignTextFieldWidget widget = new SignTextFieldWidget(
                    iconWidget.getX(), anchorY, signTextFields.get(i), ICON_SCALE, textRenderer
            );
            signTextFieldWidgets.add(addDrawableChild(widget));
            if (texts.size() <= i) continue;
            widget.setText(texts.get(i));
        }
    }

    private ButtonWidget addConfirmButton() {
        return addDrawableChild(new ButtonWidget.Builder(
                Text.translatable("clicksigns.text.confirm"),
                button -> {
                    if (!builder.templateId().equals(ClickSigns.ERROR_TEMPLATE_ID)) {
                        builder.texts(readEditorTexts());
                        ClientPlayNetworking.send(builder.build().toPayload(entity.getPos()));
                    }
                    this.close();
                }).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private ButtonWidget addTemplateChangeButton() {
        return addDrawableChild(new ButtonWidget.Builder(
                Text.translatable("clicksigns.text.change_template"),
                button -> GuiUtils.openScreen(new RoadSignTemplateSelectionScreen(this))
        ).size(BUTTON_WIDTH, BUTTON_HEIGHT).build());
    }

    private void addAlignmentButtons(int y) {
        int x = width / 2 - (int) (1.5 * BUTTON_HEIGHT);
        addAlignmentRow(x, y, 0, 1, 2);
        addAlignmentRow(x, y + BUTTON_HEIGHT, 3, 4, 5);
        addAlignmentRow(x, y + BUTTON_HEIGHT * 2, 6, 7, 8);
    }

    private void addAlignmentRow(int x, int y, int i1, int i2, int i3) {
        RoadSign.Alignment[] alignments = RoadSign.Alignment.values();
        AxisOrganizer.horizontal(x, y + BUTTON_HEIGHT / 2, true, 0)
                .organize(addAlignmentButton(alignments[i1], ALIGNMENT_ICONS[i1]))
                .organize(addAlignmentButton(alignments[i2], ALIGNMENT_ICONS[i2]))
                .organize(addAlignmentButton(alignments[i3], ALIGNMENT_ICONS[i3]));
    }

    private ButtonWidget addAlignmentButton(RoadSign.Alignment alignment, String text) {
        ButtonWidget widget = new ButtonWidget.Builder(Text.literal(text),
                button -> {
                    builder.alignment(alignment);
                    refresh();
                })
                .dimensions(0, 0, BUTTON_HEIGHT, BUTTON_HEIGHT)
                .build();
        if (alignment != builder.alignment()) {
            widget.setAlpha(NOT_SELECTED_ALPHA);
        }
        return addDrawableChild(widget);
    }

    private List<String> readEditorTexts() {
        return signTextFieldWidgets.stream()
                .map(TextFieldWidget::getText)
                .toList();
    }

    public static void openScreen(RoadSignBlockEntity entity) {
        GuiUtils.openScreen(new RoadSignEditScreen(entity));
    }
}
