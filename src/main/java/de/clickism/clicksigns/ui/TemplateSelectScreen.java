package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.*;
import de.clickism.clickui.reactivity.State;
import de.clickism.clickui.style.Border;
import net.minecraft.ChatFormatting;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TemplateSelectScreen extends UiScreen<TemplateSelectScreen> {
    private final State<Boolean> showLocal = state(false);

    private Consumer<Template> onTemplateSelected = template -> {};
    private Template selected = null;

    public TemplateSelectScreen onTemplateSelected(Consumer<Template> onTemplateSelected) {
        this.onTemplateSelected = onTemplateSelected;
        return this;
    }

    @Override
    public void build() {
        Ref<TemplateList> listRef = ref();
        Ref<TemplateInfo> infoRef = ref();

        Consumer<Template> updateSelected = (selected) -> {
            this.selected = selected;
            infoRef.get().template.update(selected);
        };

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
                                    .grow()
                                    .style(style()
                                        .when(context -> showLocal.get(), style()
                                            .alpha(GuiUtils.INACTIVE_ALPHA)))
                                    .onClick(event -> {
                                        showLocal.update(false);
                                    }),
                                button(t("💾", "clicksigns.template.category.local"))
                                    .grow()
                                    .style(style()
                                        .when(context -> !showLocal.get(), style()
                                            .alpha(GuiUtils.INACTIVE_ALPHA)))
                                    .onClick(event -> {
                                        showLocal.update(true);
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
                                    .showLocal(showLocal.get())
                                    .grow()
                                    .onTemplateSelected(updateSelected)
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
                                        showLocal.get()
                                            ? button(t("🗑", "clicksigns.template.delete"))
                                            .buttonColor(UiColor.RED)
                                            .growWidth()
                                            .onClick(event -> {
                                                // Delete template
                                                ClickSigns.LOCAL_TEMPLATE_MANAGER.deleteTemplate(selected);
                                                updateSelected.accept(null);
                                                listRef.get().invalidateTree(); // Invalidate list
                                            })
                                            : box().growWidth(), // Spacer,
                                        // Apply button
                                        button(t("🛠", "clicksigns.template.apply"))
                                            .buttonColor(UiColor.GREEN)
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

    private static class TemplateInfo extends UiComponent<TemplateInfo> {
        private final State<Template> template = state(null);

        @Override
        protected void build() {
            childGap(8);
            var template = this.template.get();
            if (template == null) {
                add(text("No template selected.")
                    .style(style()
                        .alpha(0.5f)));
                return;
            }
            // Add preview
            add(new SignView(new EditableRoadSign(template.build())));
            // Add template meta
            var meta = template.meta();
            add(box()
                .childGap(8)
                .children(
                    // Name
                    infoField(t("clicksigns.template.info.name"), meta.name()),
                    // Description (optional)
                    meta.description() != null && !meta.description().isEmpty()
                        ? infoField(t("clicksigns.template.info.description"), meta.description())
                        : null,
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
