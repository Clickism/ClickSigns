package de.clickism.clicksigns.gui.screen.template.widget;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.gui.widget.CategoryHeaderWidget;
import de.clickism.clicksigns.gui.util.NestedWidget;
import de.clickism.clicksigns.gui.util.VerticalScrollContainer;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

// TODO: Fix misaligned and overlapping when plates on left side of sign
public class TemplateList extends VerticalScrollContainer {
    private final Consumer<Template> onTemplateSelected;
    private @Nullable Template selectedTemplate;

    /**
     * Creates a new vertical scroll container.
     *
     * @param x      the x position of the container
     * @param y      the y position of the container
     * @param width  the width of the container
     * @param height the height of the container
     */
    public TemplateList(int x, int y, int width, int height, Consumer<Template> onTemplateSelected) {
        super(x, y, width, height);
        this.onTemplateSelected = onTemplateSelected;
        // Add resource templates
        // TODO: Add uncategorized symbols at the end
        SignRegistries.RESOURCE_TEMPLATES.allCategories().forEach(category -> {
            addChild(new CategoryHeaderWidget(this.width, category.name()));
            category.resolveEntries().forEach(template -> {
                addChild(new TemplateEntry(template));
            });
        });
        // Add local templates
        ClickSigns.LOCAL_TEMPLATE_MANAGER.reload(); // Reload local templates to ensure they are up to date
        var localTemplates = ClickSigns.LOCAL_TEMPLATE_MANAGER.templates();
        if (!localTemplates.isEmpty()) {
            addChild(new CategoryHeaderWidget(this.width, Component.translatable("clicksigns.template.category.local")));
            localTemplates.forEach(template -> {
                addChild(new TemplateEntry(template));
            });
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
        // Background
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, new Color(0, 0, 0, 80).getRGB());
        // Border
//        guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, new Color(255, 255, 255, 100).getRGB());
        super.renderWidget(guiGraphics, mouseX, mouseY, tickDelta);
    }

    /**
     * Widget for a template entry
     */
    public class TemplateEntry extends NestedWidget {
        private final Template template;

        public TemplateEntry(Template template) {
            super(0, 0);
            var displayName = template.identifier().getNamespace() + " : " + template.meta().name();
            int offsetX = 20;
            var stringWidget = new StringWidget(offsetX, 2, TemplateList.this.width - offsetX, 18, Component.literal(displayName), GuiUtils.font());
            stringWidget.alignLeft();
            addChildAndUpdate(stringWidget);
            this.template = template;

        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            var selected = TemplateList.this.selectedTemplate == template;
            if (this.isHovered || selected) {
                guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width - scrollbarWidth(), this.getY() + this.height, new Color(255, 255, 255, 50).getRGB());
            }
            super.renderWidget(guiGraphics, mouseX, mouseY, delta);
//            if (this.isHovered) {
//                GuiUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width - scrollbarWidth(), this.height, Color.WHITE.getRGB());
//            }
            if (selected) {
                GuiUtils.renderOutline(guiGraphics, this.getX(), this.getY(), this.width - scrollbarWidth(), this.height, Color.WHITE.getRGB());
            }
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            TemplateList.this.onTemplateSelected.accept(template);
            TemplateList.this.selectedTemplate = template;
        }
    }
}
