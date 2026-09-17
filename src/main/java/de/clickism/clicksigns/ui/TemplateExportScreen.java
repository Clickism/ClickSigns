package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.template.Template;
import de.clickism.clicksigns.sign.template.TemplateParser;
import de.clickism.clicksigns.ui.editor.editable.EditableRoadSign;
import de.clickism.clicksigns.util.JsonHandler;
import de.clickism.clickui.Ref;
import de.clickism.clickui.State;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.elements.input.Checkbox;
import de.clickism.clickui.elements.input.TextField;
import de.clickism.clickui.layout.Align;
import net.minecraft.client.Minecraft;

import java.util.stream.Collectors;

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
    private final Ref<TextField> authorField = ref();
    private final Ref<Checkbox> includeTexts = ref();

    private final State<Boolean> isValid = state(true);

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
                    fancyHeader(t("clicksigns.templateExport.header")),
                    smallHeader(t("clicksigns.template.info.name").copy()
                        .append(l("§r§c*"))),
                    memo(() -> textField()
                        .value(nextAvailableName(t("clicksigns.templateExport.newTemplate").getString(), " #%d"))
                        .maxLength(32)
                        .onValueChanged(this::updateValidity)
                        .ref(nameField)
                        .growWidth()),
                    smallHeader(t("clicksigns.template.info.author")),
                    memo(() -> textField()
                        .value(playerName())
                        .maxLength(64)
                        .onValueChanged(this::updateValidity)
                        .ref(authorField)
                        .growWidth()),
                    smallHeader(t("clicksigns.templateExport.export.header")),
                    box()
                        .horizontal()
                        .growWidth()
                        .childGap(4)
                        .crossAlign(Align.CENTER)
                        .children(
                            checkbox()
                                .ref(includeTexts),
                            text(t("clicksigns.templateExport.export.includeTexts"))
                        ),
                    button(t("💾", "clicksigns.templateExport.export.save"))
                        .buttonColor(UiColor.TEAL)
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
                    button(t("📄", "clicksigns.templateExport.export.copyJson"))
                        .buttonColor(UiColor.TEAL)
                        .disabled(!isValid.get())
                        .growWidth()
                        .onClick(event -> {
                            var json = new TemplateParser().toJson(
                                Template.Meta.placeholder(),
                                roadSign.build(),
                                includeTexts.get().checked()
                            );
                            var string = GSON.toJson(json);
                            UiUtil.copyToClipboard(string);
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
        try {
            var nameField = this.nameField.get();
            return !nameField.value().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Reads the metadata from the input fields and constructs a template meta object.
     *
     * @return a template meta object containing the name, description, and author information
     */
    private Template.Meta readMeta() {
        var name = nameField.get().value();
        var author = authorField.get().value();
        return new Template.Meta(
            name,
            author.isEmpty()
                ? null
                : author
        );
    }

    /**
     * Gets the name of the current player in the Minecraft instance.
     *
     * @return the player's name as a string, or an empty string if the player is not available
     */
    private static String playerName() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return "";
        }
        return player.getName().getString();
    }

    /**
     * Generates a unique name based on the provided base name by appending an index if necessary.
     *
     * @param baseName the base name to start with
     * @param indexFormat the format string for the index to append (e.g., "#%d")
     * @return a unique name that does not conflict with existing template names
     */
    private static String nextAvailableName(String baseName, String indexFormat) {
        ClickSigns.LOCAL_TEMPLATE_MANAGER.reload(); // Reload templates
        int index = 1;
        var names = ClickSigns.LOCAL_TEMPLATE_MANAGER.templates().stream()
            .map(template -> template.meta().name())
            .collect(Collectors.toSet());
        String newName = baseName;
        while (names.contains(newName)) {
            newName = baseName + indexFormat.formatted(index);
            index++;
        }
        return newName;
    }
}
