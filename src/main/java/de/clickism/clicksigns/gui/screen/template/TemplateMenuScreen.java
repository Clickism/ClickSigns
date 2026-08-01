package de.clickism.clicksigns.gui.screen.template;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.screen.BaseScreen;
import de.clickism.clicksigns.gui.screen.edit.widget.PanelWidget;
import de.clickism.clicksigns.gui.screen.template.widget.TemplateInfo;
import de.clickism.clicksigns.gui.screen.template.widget.TemplateList;
import de.clickism.clicksigns.gui.util.LinearLayout;
import de.clickism.clicksigns.gui.widget.ColoredButton;
import de.clickism.clicksigns.gui.widget.SignWidget;
import de.clickism.clicksigns.gui.widget.element.PlateWidget;
import de.clickism.clicksigns.gui.widget.element.SymbolWidget;
import de.clickism.clicksigns.gui.widget.element.TextWidget;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.util.ComponentUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

public class TemplateMenuScreen extends BaseScreen {
    private final Consumer<Template> onTemplateChanged;
    private @Nullable Template selectedTemplate;
    private boolean showingLocal = false;

    // TODO: Maybe add image view for templates

    public TemplateMenuScreen(@Nullable Screen parent, Consumer<Template> onTemplateChanged) {
        super(parent);
        this.onTemplateChanged = onTemplateChanged;
    }

    @Override
    protected void init() {
        var listWidth = this.width / 2;
        int marginTop = 40;
        var listHeight = this.height - marginTop;

        // Panel
        var backgroundColor = new Color(0, 0, 0, 150).getRGB();
        var outlineColor = new Color(255, 255, 255, 100).getRGB();
        var panel = new PanelWidget(0, 0, this.width, marginTop, backgroundColor, outlineColor);
        panel.onlyBottomBorder();
        addRenderableWidget(panel);

        int gap = 10;
        var switchButtonWidth = (halfWidth() - gap * 3) / 2;

        // Local and Resource buttons
        var resourceButton = new Button.Builder(ComponentUtil.translatableWithIcon("📦", "clicksigns.template.category.resource"), (button) -> {
            this.showingLocal = false;
            this.selectedTemplate = null;
            this.rebuildWidgets();
        }).bounds(gap, gap, switchButtonWidth, Button.DEFAULT_HEIGHT).build();
        resourceButton.setAlpha(showingLocal ? GuiUtils.INACTIVE_ALPHA : 1f);
        resourceButton.setTooltip(Tooltip.create(Component.translatable("clicksigns.template.category.resource.tooltip")));
        addRenderableWidget(resourceButton);

        var localButton = new Button.Builder(ComponentUtil.translatableWithIcon("💾", "clicksigns.template.category.local"), (button) -> {
            this.showingLocal = true;
            this.selectedTemplate = null;
            this.rebuildWidgets();
        }).bounds(gap + switchButtonWidth + gap, gap, switchButtonWidth, Button.DEFAULT_HEIGHT).build();
        localButton.setAlpha(showingLocal ? 1f : GuiUtils.INACTIVE_ALPHA);
        localButton.setTooltip(Tooltip.create(Component.translatable("clicksigns.template.category.local.tooltip")));
        addRenderableWidget(localButton);

        // Preview
        var preview = signWidget();
        addRenderableWidget(preview);

        var layoutX = listWidth + gap;
        var layoutY = marginTop + gap;

        var layout = LinearLayout.vertical()
                .padding(gap)
                .add(preview);
        layout.layout(layoutX, layoutY);

        // Info area
        var templateInfo = new TemplateInfo(0, 0, halfWidth() - gap * 2);
        templateInfo.setX(layoutX);
        templateInfo.setY(preview.getY() + preview.getHeight() + gap);
        templateInfo.template(selectedTemplate);
        addRenderableWidget(templateInfo);

        var buttonY = this.height - Button.DEFAULT_HEIGHT - gap * 2;
        var buttonBaseX = halfWidth() + gap;
        var buttonWidth = (halfWidth() - gap * 3) / 2;

        // Apply button
        var applyButton = new ColoredButton(buttonBaseX + buttonWidth + gap, buttonY, buttonWidth,
                Button.DEFAULT_HEIGHT, Color.GREEN,
                ComponentUtil.translatableWithIcon("🛠", "clicksigns.template.apply"), (button) -> {
            if (this.selectedTemplate != null) {
                this.onTemplateChanged.accept(this.selectedTemplate);
            }
            this.onClose();
        });
        addRenderableWidget(applyButton);

        // Local, add option to delete
        var deleteButton = new ColoredButton(buttonBaseX, buttonY, buttonWidth,
                Button.DEFAULT_HEIGHT, Color.RED,
                ComponentUtil.translatableWithIcon("🗑", "clicksigns.template.delete"), (button) -> {
            ClickSigns.LOCAL_TEMPLATE_MANAGER.deleteTemplate(selectedTemplate);
            this.selectedTemplate = null;
            this.rebuildWidgets();
        });
        deleteButton.visible = false;
        addRenderableWidget(deleteButton);

        // List
        var list = new TemplateList(0, marginTop, listWidth, listHeight, (template) -> {
            this.selectedTemplate = template;
            // Reset layout to recalculate size
            layout.layout(layoutX, layoutY);
            // Update preview
            preview.roadSign(template.build());
            var offsetX = layoutX - preview.minX();
            var offsetY = layoutY - preview.minY();
            layout.layout(layoutX + offsetX, layoutY + offsetY);
            // Update info
            templateInfo.setX(layoutX);
            templateInfo.setY(preview.getY() + preview.getHeight() + gap);
            templateInfo.template(template);

            // Add delete button if local
            deleteButton.visible = showingLocal && ClickSigns.LOCAL_TEMPLATE_MANAGER.isLocal(template);
        }, showingLocal);
        addRenderableWidget(list);
    }

    private SignWidget signWidget() {
        var roadSign = selectedTemplate != null ? selectedTemplate.build() : null;
        var preview = new SignWidget(0, 0, roadSign,
                // No outline
                (anchorX, anchorY, element, sign, parent) ->
                        new TextWidget(anchorX, anchorY, element, sign.colorResolver(), sign.width(), 0),
                (anchorX, anchorY, element, sign, screen) ->
                        new SymbolWidget(anchorX, anchorY, element, sign.colorResolver(), 0, screen),
                (anchorX, anchorY, element, sign, parent) ->
                        new PlateWidget(anchorX, anchorY, element, sign.colorResolver()),
                null);
        preview.setActive(false);
        return preview;
    }
}
