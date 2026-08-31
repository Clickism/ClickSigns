package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.reactivity.State;
import de.clickism.clickui.style.BorderPosition;
import de.clickism.clickui.style.Style;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;
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
                box.add(category(t("clicksigns.template.category.local")));
                localTemplates.forEach(template -> {
                    box.add(entry(template));
                });
            }
        } else {
            // Add resource templates
            // TODO: Add uncategorized templates at the end
            SignRegistries.RESOURCE_TEMPLATES.allCategories().forEach(category -> {
                box.add(category(l(category.name())));
                category.resolveEntries().forEach(template -> {
                    box.add(entry(template));
                });
            });
        }
    }

    private UiElement<?> category(Component name) {
        return box()
            .padding(8, 24)
            .growWidth()
            .children(
                box()
                    .padding(4)
                    .growWidth()
                    .alignCenter()
                    .overrideStyle(Style.empty()
                        .border(UiColor.LIGHT_GRAY)
                        .background(UiColor.BLACK_A50))
                    .children(
                        text(name)
                    )
            );
    }

    private UiElement<?> entry(Template template) {
        return text(template.identifier().getNamespace() + " : " + template.meta().name())
            .padding(5, 12, 4, 12)
            .growWidth()
            .tooltip(new SignView(new EditableRoadSign(template.build())))
            .style(s -> s
                .whenHovered(h -> h
                    .background(UiColor.WHITE_A30))
                .when(context -> selected == template, l -> l
                    .border(UiColor.WHITE)
                    .borderPosition(BorderPosition.INSIDE)
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
