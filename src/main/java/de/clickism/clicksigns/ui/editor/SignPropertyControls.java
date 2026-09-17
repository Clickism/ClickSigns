package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.element.TextStyle;
import de.clickism.clicksigns.ui.TemplateExportScreen;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clicksigns.ui.components.TwoSidedTextureButton;
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
            new TwoSidedTextureButton(roadSign.frontSource(), roadSign.backSource())
                .onFrontSelected(source -> {
                    roadSign.frontSource(source.resize(roadSign.size()));
                })
                .onBackSelected(source -> {
                    roadSign.backSource(source.resize(roadSign.size()));
                })
                .maskBack(),
            // Add element controls
            smallHeader(t("clicksigns.editor.sign.elements.header")),

            button(t("+", "clicksigns.editor.sign.elements.addSymbol"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var center = roadSign.center();
                    var symbol = SignRegistries.SYMBOLS.get(RoadSign.DEFAULT_SYMBOL_TEXTURE);
                    var element = new SymbolElement(
                        center.x(), center.y(), Alignment.CENTER,
                        symbol
                    );
                    roadSign.addElement(element);
                }),
            button(t("+", "clicksigns.editor.sign.elements.addText"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var center = roadSign.center();
                    var element = new TextElement(
                        center.x(), center.y(), Alignment.CENTER,
                        "", 1.0f, TextStyle.DEFAULT
                    );
                    roadSign.addElement(element);
                }),
            button(t("+", "clicksigns.editor.sign.elements.addPlate"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var center = roadSign.center();
                    var element = new PlateElement(
                        center.x(), center.y(), Alignment.CENTER,
                        roadSign.frontSource().resize(8, 6),
                        roadSign.backSource().resize(8, 6),
                        true
                    );
                    roadSign.addElement(element);
                }),
            // Add tools
            smallHeader(t("clicksigns.editor.sign.tools.header")),
            button(t("⏪", "clicksigns.editor.sign.tools.resetTexts"))
                .growWidth()
                .buttonColor(UiColor.ORANGE)
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
}
