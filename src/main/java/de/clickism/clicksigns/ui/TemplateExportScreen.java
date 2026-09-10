package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.TemplateParser;
import de.clickism.clicksigns.ui.editor.EditableRoadSign;
import de.clickism.clicksigns.util.JsonHandler;
import de.clickism.clickui.Ref;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.elements.input.Checkbox;
import de.clickism.clickui.elements.input.TextField;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.reactivity.State;

import static de.clickism.clicksigns.util.ComponentUtil.l;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * Represents a screen for exporting a road sign as a template.
 * <p>
 * This screen allows the user to input metadata for the template, such as name, description, and author,
 * and provides options to include texts in the exported template.
 * <p>
 * The template can then be saved to the local template manager or copied as JSON to the clipboard.
 */
public class TemplateExportScreen extends UiScreen<TemplateExportScreen>
    implements FancyHeaders, JsonHandler {

    private final Ref<TextField> nameField = ref();
    private final Ref<TextField> descriptionField = ref();
    private final Ref<TextField> authorField = ref();
    private final Ref<Checkbox> includeTexts = ref();

    private final State<Boolean> isValid = state(false);

    private final EditableRoadSign roadSign;

    /**
     * Creates a new instance of the TemplateExportScreen with the specified EditableRoadSign.
     *
     * @param roadSign the EditableRoadSign to be exported as a template
     */
    public TemplateExportScreen(EditableRoadSign roadSign) {
        this.roadSign = roadSign;
    }

    @Override
    protected void build() {
        alignCenter();
        grow();
        children(
            box()
                .padding(8)
                .childGap(4)
                .minWidth(200)
                .alignCenter()
                .style(style()
                    .backgroundColor(UiColor.BLACK_A40)
                    .borderColor(UiColor.WHITE_A30))
                .children(
                    fancyHeader(l("Export Template")),
                    smallHeader(t("clicksigns.template.info.name").copy()
                        .append(l("§r§c*"))),
                    memo(() -> textField()
                        .maxLength(32)
                        .onValueChanged(this::updateValidity)
                        .ref(nameField)
                        .growWidth()),
                    smallHeader(t("clicksigns.template.info.description")),
                    memo(() -> textField()
                        .maxLength(512)
                        .onValueChanged(this::updateValidity)
                        .ref(descriptionField)
                        .growWidth()), // TODO: Text area for description
                    smallHeader(t("clicksigns.template.info.author")),
                    memo(() -> textField()
                        .maxLength(64)
                        .onValueChanged(this::updateValidity)
                        .ref(authorField)
                        .growWidth()),
                    smallHeader(t("clicksigns.editor.export")),
                    box()
                        .horizontal()
                        .growWidth()
                        .childGap(4)
                        .crossAlign(Align.CENTER)
                        .children(
                            checkbox()
                                .ref(includeTexts),
                            text(t("clicksigns.template.include_texts"))
                        ),
                    button(t("💾", "clicksigns.editor.export.save_template"))
                        .buttonColor(UiColor.CYAN)
                        .disabled(!isValid.get())
                        .growWidth()
                        .onClick(event -> {
                            ClickSigns.LOCAL_TEMPLATE_MANAGER.saveAsTemplate(
                                readMeta(),
                                roadSign.build(),
                                includeTexts.get().checked()
                            );
                            event.screen().close();
                        }),
                    button(t("📄", "clicksigns.editor.export.copy_json"))
                        .buttonColor(UiColor.CYAN)
                        .disabled(!isValid.get())
                        .growWidth()
                        .onClick(event -> {
                            var json = new TemplateParser().toJson(
                                Template.Meta.placeholder(),
                                roadSign.build(),
                                includeTexts.get().checked()
                            );
                            var string = GSON.toJson(json);
                            GuiUtils.copyToClipboard(string);
                        })
                )
        );
    }

    /**
     * Updates the validity state based on the current input values.
     *
     * @param newValue the new value of the input field that triggered the update
     */
    private void updateValidity(String newValue) {
        isValid.update(isValidInput());
    }

    /**
     * Checks if the input fields for name, description, and author are valid.
     *
     * @return true if the input is valid, false otherwise
     */
    private boolean isValidInput() {
        var nameField = this.nameField.getOrNull();
        if (nameField == null) return false;
        return !nameField.value().isEmpty();
    }

    /**
     * Reads the metadata from the input fields and constructs a template meta object.
     *
     * @return a template meta object containing the name, description, and author information
     */
    private Template.Meta readMeta() {
        var name = nameField.get().value();
        var description = descriptionField.get().value();
        var author = authorField.get().value();
        return new Template.Meta(
            name,
            description.isEmpty()
                ? null
                : description,
            author.isEmpty()
                ? null
                : author
        );
    }
}
