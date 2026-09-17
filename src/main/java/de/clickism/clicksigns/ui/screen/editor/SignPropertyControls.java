package de.clickism.clicksigns.ui.screen.editor;

import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SignElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.components.TwoSidedTextureButton;
import de.clickism.clicksigns.ui.screen.template.TemplateExportScreen;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;

import java.util.ArrayList;

import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * The sign controls, for editing general info about this sign,
 * such as textures or adding elements.
 */
class SignPropertyControls extends UiComponent<SignPropertyControls> implements CommonComponents {
    private final SignEditorContext context;

    public SignPropertyControls(SignEditorContext context) {
        this.context = context;
    }

    @Override
    protected void build() {
        childGap(4);
        var roadSign = context.roadSign();
        children(
            fancyHeader(t("clicksigns.editor.sign.header")),
            // Add texture selection
            smallHeader(t("clicksigns.editor.sign.textures")),
            new TwoSidedTextureButton(
                roadSign.frontSource(),
                roadSign.backSource(),
                roadSign.colorResolver(),
                roadSign.size()
            )
                .onFrontSelected(roadSign::frontSource)
                .onBackSelected(roadSign::backSource)
                .maskBack(),
            // Add element controls
            smallHeader(t("clicksigns.editor.sign.elements.header")),

            button(t("+", "clicksigns.editor.sign.elements.addSymbol"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    spawnElement(SymbolElement.createDefault());
                }),
            button(t("+", "clicksigns.editor.sign.elements.addText"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    spawnElement(TextElement.createDefault());
                }),
            button(t("+", "clicksigns.editor.sign.elements.addPlate"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var plate = PlateElement.createDefault();
                    spawnElement(plate
                        // Use sign textures but match the default size
                        .withFrontSource(roadSign.frontSource().resize(plate.size()))
                        .withBackSource(roadSign.backSource().resize(plate.size())));
                }),
            // Add tools
            smallHeader(t("clicksigns.editor.sign.tools.header")),
            button(t("⏪", "clicksigns.editor.sign.tools.resetTexts"))
                .growWidth()
                .buttonColor(UiColor.MAROON)
                .onClick(event -> {
                    var elements = new ArrayList<>(roadSign.elements());
                    for (var element : elements) {
                        if (element.current() instanceof TextElement) {
                            roadSign.updateElement(element.id(),
                                edited -> ((TextElement) edited).withText(""));
                            // Regenerate id so that all caches are reset
                            roadSign.regenerateId(element.id());
                        }
                    }
                }),
            button(t("🗑", "clicksigns.editor.sign.tools.removeElements"))
                .growWidth()
                .buttonColor(UiColor.MAROON)
                .onClick(event -> {
                    var elements = new ArrayList<>(roadSign.elements());
                    for (var element : elements) {
                        roadSign.removeElement(element.id());
                    }
                }),
            smallHeader(t("clicksigns.editor.sign.export.header")),
            button(t("📤", "clicksigns.editor.sign.export.exportTemplate"))
                .growWidth()
                .buttonColor(UiColor.TEAL)
                .onClick(event -> {
                    new TemplateExportScreen(roadSign).open();
                })
        );
    }

    /**
     * Spawns a new element in the center of the sign.
     *
     * @param element the element to spawn
     */
    private void spawnElement(SignElement element) {
        var position = context.roadSign().center();
        element = element.withPosition(position.x(), position.y());
        var editable = context.roadSign().addElement(element);
        context.setSelected(editable);
    }
}
