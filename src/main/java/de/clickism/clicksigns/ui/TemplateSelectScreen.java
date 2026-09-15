package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.elements.SignTextField;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.*;
import de.clickism.clickui.reactivity.State;
import de.clickism.clickui.style.Border;
import net.minecraft.ChatFormatting;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TemplateSelectScreen extends UiScreen<TemplateSelectScreen> {
    private final State<Boolean> showLocal = state(false);
    private final State<Template> selected = state(null);

    private Consumer<Template> onTemplateSelected = template -> {};
    private boolean reloadedLocal = false;

    public TemplateSelectScreen onTemplateSelected(Consumer<Template> onTemplateSelected) {
        this.onTemplateSelected = onTemplateSelected;
        return this;
    }

    @Override
    public void build() {
        Ref<TemplateList> listRef = ref();
        Ref<TemplateInfo> infoRef = ref();

        if (showLocal.get() && !reloadedLocal) {
            reloadedLocal = true;
            // Reload local templates to ensure they are up to date
            ClickSigns.LOCAL_TEMPLATE_MANAGER.reload();
        }

        var selected = this.selected.get();
        this.grow()
            .children(
                // Top Bar
                box()
                    .padding(8, 8)
                    .style(style()
                        .borderPositionBottom(Border.Position.INSIDE)
                        .borderColorBottom(UiColor.LIGHT_GRAY)
                        .backgroundColor(UiColor.BLACK_A50))
                    .horizontal()
                    .growWidth()
                    .children(
                        box()
                            .horizontal()
                            .growWidth()
                            .childGap(8)
                            .children(
                                button(t("📦", "clicksigns.template.category.resource"))
                                    .tooltip(t("clicksigns.template.category.resource.tooltip"))
                                    .grow()
                                    .style(style()
                                        .when(context -> showLocal.get(), style()
                                            .alpha(UiConstants.INACTIVE_ALPHA)))
                                    .onClick(event -> {
                                        showLocal.update(false);
                                        this.selected.update(o -> null); // Clear selection when switching to resource templates
                                    }),
                                button(t("💾", "clicksigns.template.category.local"))
                                    .tooltip(t("clicksigns.template.category.local.tooltip"))
                                    .grow()
                                    .style(style()
                                        .when(context -> !showLocal.get(), style()
                                            .alpha(UiConstants.INACTIVE_ALPHA)))
                                    .onClick(event -> {
                                        showLocal.update(true);
                                        this.selected.update(o -> null); // Clear selection when switching to resource templates
                                    })
                            ),

                        box().growWidth()
                    ),

                box()
                    .horizontal()
                    .grow()
                    .children(
                        // Template List
                        box()
                            .grow()
                            .style(style()
                                .backgroundColor(UiColor.BLACK_A30))
                            .children(
                                new TemplateList()
                                    .ref(listRef)
                                    .selected(this.selected.get())
                                    .showLocal(showLocal.get())
                                    .grow()
                                    .onTemplateSelected(this.selected::update)
                            ),

                        // Preview
                        box()
                            .grow()
                            .padding(16)
                            .children(
                                // Template info
                                new TemplateInfo()
                                    .ref(infoRef),
                                // Spacer
                                box().grow(),
                                // Buttons
                                box()
                                    .horizontal()
                                    .growWidth()
                                    .childGap(8)
                                    .children(
                                        // Delete button
                                        showLocal.get() && selected != null
                                            ? button(t("🗑", "clicksigns.template.delete"))
                                            .buttonColor(UiColor.MAROON)
                                            .growWidth()
                                            .onClick(event -> {
                                                // Delete template
                                                ClickSigns.LOCAL_TEMPLATE_MANAGER.deleteTemplate(selected);
                                                // Find the next template to select
                                                var templateList = listRef.get();
                                                var templates = templateList.templates();
                                                var index = templates.indexOf(selected);
                                                var nextIndex = index < templates.size() - 1
                                                    ? index + 1
                                                    : index - 1;
                                                var nextTemplate = nextIndex >= 0 && nextIndex < templates.size()
                                                    ? templates.get(nextIndex)
                                                    : null;
                                                this.selected.update(o -> nextTemplate);
                                                templateList.invalidateTree(); // Invalidate list
                                            })
                                            : box().growWidth(), // Spacer,
                                        // Apply button
                                        button(t("🛠", "clicksigns.template.apply"))
                                            .buttonColor(UiColor.LIME)
                                            .growWidth()
                                            .onClick(event -> {
                                                // Call callback and close screen
                                                if (selected != null) {
                                                    onTemplateSelected.accept(selected);
                                                }
                                                close();
                                            })
                                    )
                            )
                    )
            );
    }

    private class TemplateInfo extends UiComponent<TemplateInfo> {
        @Override
        protected void build() {
            childGap(8);
            var template = selected.get();
            if (template == null) {
                add(text("No template selected.")
                    .style(style()
                        .alpha(0.5f)));
                return;
            }
            // Add preview
            add(new SignView(new EditableRoadSign(template.build()))
                .elementConfig((uiElement, signElement) -> {
                    if (uiElement instanceof SignTextField textField) {
                        // Disable input for the preview
                        textField.allowInput(false);
                    }
                }));
            // Add template meta
            var meta = template.meta();
            add(box()
                .childGap(8)
                .children(
                    // Name
                    infoField(t("clicksigns.template.info.name"), meta.name()),
                    // Author (optional)
                    meta.author() != null && !meta.author().isEmpty()
                        ? infoField(t("clicksigns.template.info.author"), meta.author())
                        : null
                ));
        }

        private UiElement<?> infoField(
            net.minecraft.network.chat.Component label,
            String value
        ) {
            return box()
                .style(style()
                    .backgroundColor(UiColor.BLACK_A50))
                .padding(4)
                .childGap(2)
                .maxWidth(300)
                .children(
                    text(label.copy().withStyle(ChatFormatting.GRAY))
                        .style(style()
                            .fontScale(0.7f)),
                    text(value)
                );
        }
    }
}
