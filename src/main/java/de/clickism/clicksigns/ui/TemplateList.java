package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.*;
import de.clickism.clickui.reactivity.State;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TemplateList extends UiComponent<TemplateList> {

    private Consumer<Template> onTemplateSelected = template -> {};
    private final State<Boolean> showLocal = state(false);
    private @Nullable Template selected = null;

    @Override
    protected void build() {
        // Scrollable box
        var box = box()
            .grow()
            .scrollable(true);
        add(box);

        if (showLocal.get()) {
            // Add local templates
            ClickSigns.LOCAL_TEMPLATE_MANAGER.reload(); // Reload local templates to ensure they are up to date
            var localTemplates = ClickSigns.LOCAL_TEMPLATE_MANAGER.templates();
            if (!localTemplates.isEmpty()) {
                box.add(h4(t("clicksigns.template.category.local"))
                    .growWidth()
                    .alignTextCenter());
                localTemplates.forEach(template -> {
                    box.add(entry(template));
                });
            }
        } else {
            // Add resource templates
            // TODO: Add uncategorized templates at the end
            SignRegistries.RESOURCE_TEMPLATES.allCategories().forEach(category -> {
                box.add(h4(category.name())
                    .growWidth()
                    .alignTextCenter());
                category.resolveEntries().forEach(template -> {
                    box.add(entry(template));
                });
            });
        }
    }

    private UiElement<?> entry(Template template) {
        return text(template.identifier().getNamespace() + " : " + template.meta().name())
            .padding(4, 24)
            .growWidth()
            .tooltip(new SignView()
                .roadSign(template.build()))
            .style(s -> s
                .whenHovered(h -> h
                    .background(UiColor.WHITE_A30))
                .when(context -> selected == template, l -> l
                    .border(UiColor.WHITE)
                    .background(UiColor.WHITE_A30)))
            .onClick(event -> {
                selected = template;
                onTemplateSelected.accept(template);
            });
    }

    public TemplateList onTemplateSelected(Consumer<Template> onTemplateSelected) {
        this.onTemplateSelected = onTemplateSelected;
        return this;
    }

    public TemplateList showLocal(boolean showLocal) {
        this.showLocal.update(showLocal);
        return this;
    }
}
