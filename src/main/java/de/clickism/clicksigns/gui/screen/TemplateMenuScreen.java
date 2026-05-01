package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.layout.LinearLayout;
import de.clickism.clicksigns.gui.widget.SignPreviewWidget;
import de.clickism.clicksigns.gui.widget.TemplateList;
import de.clickism.clicksigns.sign.template.Template;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TemplateMenuScreen extends BaseScreen {
    private final Consumer<Template> onTemplateChanged;
    private @Nullable Template selectedTemplate;

    public TemplateMenuScreen(@Nullable Screen parent, Consumer<Template> onTemplateChanged) {
        super(parent);
        this.onTemplateChanged = onTemplateChanged;
    }

    @Override
    protected void init() {
        var listWidth = this.width / 2;
        int marginTop = 40;
        var listHeight = this.height - marginTop;

        // Preview
        var roadSign = selectedTemplate != null ? selectedTemplate.buildDefault() : null;
        var preview = new SignPreviewWidget(0, 0, roadSign);
        addRenderableWidget(preview);

        var layoutX = listWidth + 10;
        var layoutY = marginTop;

        var layout = LinearLayout.vertical()
                .padding(8)
                .add(preview);
        layout.layout(layoutX, layoutY);
        // List
        var list = new TemplateList(0, marginTop, listWidth, listHeight, (template) -> {
            this.selectedTemplate = template;
            // Update preview
            preview.roadSign(template.buildDefault());
            layout.layout(layoutX, layoutY);
        });
        addRenderableWidget(list);

        var confirmButton = confirmButton();
        confirmButton.setX(this.width - confirmButton.getWidth() - 10);
        confirmButton.setY(this.height - confirmButton.getHeight() - 10);
        addRenderableWidget(confirmButton);
    }

    private Button confirmButton() {
        return Button.builder(Component.literal("Confirm"), (button) -> {
            if (this.selectedTemplate != null) {
                this.onTemplateChanged.accept(this.selectedTemplate);
            }
            this.onClose();
        }).build();
    }
}
