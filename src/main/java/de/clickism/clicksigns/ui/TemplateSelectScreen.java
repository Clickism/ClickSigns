package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.ui.elements.SignView;
import de.clickism.clickui.*;
import de.clickism.clickui.reactivity.State;
import net.minecraft.ChatFormatting;

import java.util.function.Consumer;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TemplateSelectScreen extends UiScreen {
    private boolean showLocal = false;

    private Consumer<Template> onTemplateSelected = template -> {};
    private Template selected = null;

    public TemplateSelectScreen onTemplateSelected(Consumer<Template> onTemplateSelected) {
        this.onTemplateSelected = onTemplateSelected;
        return this;
    }

    @Override
    public Element<?> build() {
        Ref<TemplateList> listRef = ref();
        Ref<TemplateInfo> infoRef = ref();

        Consumer<Template> updateSelected = (selected) -> {
            this.selected = selected;
            infoRef.get().template.update(selected);
        };

        return box()
            .grow()
            .children(
                // Top Bar
                box()
                    .padding(0, 8)
                    .style(s -> s
                        .border(UiColor.LIGHT_GRAY)
                        .background(UiColor.BLACK_A50))
                    .horizontal()
                    .growWidth()
                    .children(
                        box()
                            .horizontal()
                            .growWidth()
                            .padding(8)
                            .childGap(8)
                            .children(
                                button(t("📦", "clicksigns.template.category.resource"))
                                    .grow()
                                    .style(s -> s
                                        .when(context -> showLocal, l -> l
                                            .alpha(GuiUtils.INACTIVE_ALPHA)))
                                    .onClick(event -> {
                                        showLocal = false;
                                        listRef.get().showLocal(showLocal);
                                    }),
                                button(t("💾", "clicksigns.template.category.local"))
                                    .grow()
                                    .style(s -> s
                                        .when(context -> !showLocal, l -> l
                                            .alpha(GuiUtils.INACTIVE_ALPHA)))
                                    .onClick(event -> {
                                        showLocal = true;
                                        listRef.get().showLocal(showLocal);
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
                            .style(s -> s
                                .background(UiColor.BLACK_A30))
                            .children(
                                new TemplateList()
                                    .ref(listRef)
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
                                        showLocal
                                            ? button(t("🗑", "clicksigns.template.delete"))
                                            .growWidth()
                                            .onClick(event -> {
                                                // Delete template
                                                ClickSigns.LOCAL_TEMPLATE_MANAGER.deleteTemplate(selected);
                                                updateSelected.accept(null);
                                                listRef.get().invalidate(); // Invalidate list
                                            })
                                            : box().growWidth(), // Spacer,
                                        // Apply button
                                        button(t("🛠", "clicksigns.template.apply"))
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

    private static class TemplateInfo extends Component<TemplateInfo> {
        private final State<Template> template = state(null);

        @Override
        protected void build() {
            var template = this.template.get();
            if (template == null) {
                add(text("No template selected.")
                    .style(s -> s.alpha(0.5f)));
                return;
            }
            // Add preview
            add(new SignView()
                .roadSign(template.build()));
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

        private Element<?> infoField(
            net.minecraft.network.chat.Component label,
            String value
        ) {
            return box()
                .style(s -> s
                    .background(UiColor.BLACK_A30))
                .padding(8)
                .children(
                    text(label.copy().withStyle(ChatFormatting.GRAY))
                        .style(s -> s
                            .fontScale(0.75f)),
                    text(value)
                );
        }
    }
}
