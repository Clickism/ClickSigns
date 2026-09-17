package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.editor.editable.EditableRoadSign;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.UiElement;
import de.clickism.clickui.State;
import de.clickism.clickui.style.Border;
import de.clickism.clickui.style.Style;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TemplateList extends UiComponent<TemplateList> implements FancyHeaders {

    private final State<Boolean> showLocal = state(false);
    private Consumer<Template> onTemplateSelected = template -> {};
    private @Nullable Template selected = null;

    private final List<Template> templates = new ArrayList<>();

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
            var localTemplates = ClickSigns.LOCAL_TEMPLATE_MANAGER.templates();
            if (!localTemplates.isEmpty()) {
                box.add(category(t("clicksigns.template.category.local")));
                localTemplates.forEach(template -> {
                    box.add(entry(template));
                    templates.add(template);
                });
            }
        } else {
            // Add resource templates
            SignRegistries.RESOURCE_TEMPLATES.allCategories().forEach(category -> {
                var entries = category.resolveEntries();
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
                    new SignView(new EditableRoadSign(template.build()))
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
