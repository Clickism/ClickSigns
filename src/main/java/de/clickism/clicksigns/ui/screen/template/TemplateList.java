package de.clickism.clicksigns.ui.screen.template;

import de.clickism.clicksigns.ClickSignsClient;
import de.clickism.clicksigns.registry.Category;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.components.sign.SignView;
import de.clickism.clicksigns.ui.editable.EditableRoadSign;
import de.clickism.clickui.State;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.style.Border;
import de.clickism.clickui.style.Style;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TemplateList extends UiComponent<TemplateList> implements CommonComponents {

    private final State<Boolean> showLocal = state(false);
    private final List<Template> templates = new ArrayList<>();
    private Consumer<Template> onTemplateSelected = template -> {};
    private @Nullable Template selected = null;

    @Override
    protected void build() {
        // Scrollable box
        var box = box()
            .grow()
            .scrollable(true);
        add(box);

        templates.clear();
        if (showLocal.get()) {
            // Add local templates
            var localTemplates = ClickSignsClient.LOCAL_TEMPLATE_MANAGER.templates().stream()
                .sorted(Comparator.comparing(template -> template.meta().name()))
                .toList();
            if (!localTemplates.isEmpty()) {
                box.add(category(t("clicksigns.template.category.local")));
                localTemplates.forEach(template -> {
                    box.add(entry(template));
                    templates.add(template);
                });
            }
        } else {
            // Add resource templates
            SignRegistries.RESOURCE_TEMPLATES.allCategories().stream()
                .sorted(Comparator.comparing(Category::name))
                .forEach(category -> {
                    var entries = category.resolveEntries().stream()
                        .sorted(Comparator.comparing(template -> template.meta().name()))
                        .toList();
                    if (entries.isEmpty()) return;
                    box.add(category(l(category.name())));
                    entries.forEach(template -> {
                        box.add(entry(template));
                        templates.add(template);
                    });
                });
        }
    }

    public List<Template> templates() {
        return templates;
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
                        .borderColor(UiColor.LIGHT_GRAY)
                        .backgroundColor(UiColor.BLACK_A50))
                    .children(
                        text(name)
                    )
            );
    }

    private UiElement<?> entry(Template template) {
        return text(template.identifier().getNamespace() + " : " + template.meta().name())
            .padding(5, 12, 4, 12)
            .growWidth()
            .tooltip(box()
                .padding(4)
                .childGap(4)
                .children(
                    smallHeader(l(template.meta().name())).padding(0),
                    new SignView(new EditableRoadSign(template.roadSign()))
                ))
            .style(style()
                .whenHovered(style()
                    .backgroundColor(UiColor.WHITE_A30))
                .when(context -> selected == template, style()
                    .borderColor(UiColor.WHITE)
                    .borderPosition(Border.Position.INSIDE)
                    .backgroundColor(UiColor.WHITE_A30)))
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

    public TemplateList selected(@Nullable Template template) {
        this.selected = template;
        return this;
    }
}
