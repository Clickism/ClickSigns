package de.clickism.clicksigns.gui.screen.edit;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.screen.edit.widget.PanelWidget;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.ColoredButton;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.TemplateParser;
import de.clickism.clicksigns.util.ComponentUtil;
import de.clickism.clicksigns.util.JsonHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TemplateExportScreen extends BaseScreen implements JsonHandler {
    private final RoadSign roadSign;

    private EditBox nameBox;
    private MultiLineEditBox descriptionBox;
    private EditBox authorBox;

    private final List<ColoredButton> exportButtons = new ArrayList<>();

    protected TemplateExportScreen(@Nullable Screen parent, RoadSign roadSign) {
        super(parent);
        this.roadSign = roadSign;
    }

    @Override
    protected void init() {
        exportButtons.clear();
        var layout = LinearLayout.vertical()
                .center()
                .padding(8);
        var composer = layout.composer(this.width);

        composer.header(Component.translatable("clicksigns.template.info.name")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE)
                .append(Component.literal("§r§c*")), false);

        var boxWidth = 200;
        nameBox = new EditBox(this.font, 0, 0, boxWidth, 20, Component.empty());
        nameBox.setMaxLength(32);
        nameBox.setResponder(value -> updateValidity());
        composer.widget(nameBox);

        composer.header(Component.translatable("clicksigns.template.info.description"));
        descriptionBox = new MultiLineEditBox(this.font, 0, 0, boxWidth, 60, Component.empty(), Component.empty());
        descriptionBox.setCharacterLimit(512);
        descriptionBox.setValueListener(value -> updateValidity());
        composer.widget(descriptionBox);

        composer.header(Component.translatable("clicksigns.template.info.author"));
        authorBox = new EditBox(this.font, 0, 0, boxWidth, 20, Component.empty());
        var player = Minecraft.getInstance().player;
        authorBox.setValue(player != null ? player.getName().getString() : "");
        authorBox.setResponder(value -> updateValidity());
        authorBox.setMaxLength(64);
        composer.widget(authorBox);

        // TODO: Include texts button

        composer.header(Component.translatable("clicksigns.editor.export"));

        var includeTextsBox = new Checkbox(0, 0, boxWidth, Button.DEFAULT_HEIGHT, Component.translatable("clicksigns.template.include_texts"), false);
        composer.widget(includeTextsBox);

        var saveTemplateButton = new ColoredButton(0, 0, boxWidth, Button.DEFAULT_HEIGHT, Color.CYAN, ComponentUtil.t("💾", "clicksigns.editor.export.save_template"), button -> {
            // TODO: Separate Menu and ask for name, desc and author, then save as template
            ClickSigns.LOCAL_TEMPLATE_MANAGER.saveAsTemplate(
                    readMeta(),
                    roadSign,
                    includeTextsBox.selected()
            );
            this.onClose();
        });

        var copyJsonButton = new ColoredButton(0, 0, boxWidth, Button.DEFAULT_HEIGHT, Color.CYAN, ComponentUtil.t("📄", "clicksigns.editor.export.copy_json"), button -> {
            var json = new TemplateParser().toJson(
                    Template.Meta.placeholder(),
                    roadSign,
                    includeTextsBox.selected()
            );
            var string = GSON.toJson(json);
            GuiUtils.copyToClipboard(string);
        });

        composer.widget(saveTemplateButton);
        composer.widget(copyJsonButton);

        exportButtons.add(saveTemplateButton);
        exportButtons.add(copyJsonButton);

        composer.layout(halfWidth(), halfHeight());

        int margin = 8;
        var firstElement = layout.children().get(0);
        int minX = halfWidth() - boxWidth / 2;
        int minY = firstElement.getY();
        var lastElement = layout.children().get(layout.children().size() - 1);
        int maxY = lastElement.getY() + lastElement.getHeight();
        var panel = new PanelWidget(minX - margin, minY - margin, boxWidth + margin * 2, maxY - minY + margin * 2);
//        panel.borders(false, false, false, false);
        addRenderableWidget(panel);

        composer.compose(this::addRenderableWidget);

        this.updateValidity();
    }

    private void updateValidity() {
        if (isValidInput()) {
            exportButtons.forEach(button -> {
                button.active = true;
                button.setAlpha(1.0f);
            });
        } else {
            exportButtons.forEach(button -> {
                button.active = false;
                button.setAlpha(GuiUtils.INACTIVE_ALPHA);
            });
        }
    }

    private boolean isValidInput() {
        return !nameBox.getValue().isEmpty();
    }

    private Template.Meta readMeta() {
        var name = nameBox.getValue();
        var description = descriptionBox.getValue();
        var author = authorBox.getValue();
        return new Template.Meta(name, description.isEmpty() ? null : description, author.isEmpty() ? null : author);
    }
}
