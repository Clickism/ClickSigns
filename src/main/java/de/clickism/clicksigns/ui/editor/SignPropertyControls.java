package de.clickism.clicksigns.ui.editor;

import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.Alignment;
import de.clickism.clicksigns.sign.RoadSign;
import de.clickism.clicksigns.sign.element.PlateElement;
import de.clickism.clicksigns.sign.element.SymbolElement;
import de.clickism.clicksigns.sign.element.TextElement;
import de.clickism.clicksigns.sign.element.TextStyle;
import de.clickism.clicksigns.ui.FancyHeaders;
import de.clickism.clicksigns.ui.TemplateExportScreen;
import de.clickism.clicksigns.ui.TextureButton;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;

import java.util.ArrayList;

import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * The sign controls, for editing general info about this sign,
 * such as textures or adding elements.
 */
class SignPropertyControls extends UiComponent<SignPropertyControls> implements FancyHeaders {
    private final SignEditorContext context;

    public SignPropertyControls(SignEditorContext context) {
        this.context = context;
    }

    @Override
    protected void build() {
        childGap(4);
        children(
            fancyHeader(t("clicksigns.editor.sign.header")),
            // Add texture selection
            smallHeader(t("clicksigns.editor.sign.textures")),
            box()
                .horizontal()
                .growWidth()
                .childGap(4)
                .children(
                    box()
                        .growWidth()
                        .childGap(4)
                        .children(
                            smallHeader(t("clicksigns.ui.textures.front")).padding(0),
                            new TextureButton(context.roadSign().frontSource(), newTexture -> {
                                context.roadSign().frontSource(newTexture.resize(context.roadSign().build()));
                                // Update all plate elements that match the sign textures
                                for (var element : context.roadSign().elements()) {
                                    if (element.current() instanceof PlateElement plate && plate.matchSignTextures()) {
                                        context.roadSign().updateElement(element.id(),
                                            edited -> ((PlateElement) edited)
                                                .withFrontSource(newTexture.resize(plate.size())));
                                    }
                                }
                            })
                        ),
                    box()
                        .growWidth()
                        .childGap(4)
                        .children(
                            smallHeader(t("clicksigns.ui.textures.back")).padding(0),
                            new TextureButton(
                                RoadSign.maskedBackOf(context.roadSign().frontSource().resize(16, 16), context.roadSign().backSource()),
                                newTexture -> {
                                    context.roadSign().backSource(newTexture.resize(context.roadSign().build()));
                                    // Update all plate elements that match the sign textures
                                    for (var element : context.roadSign().elements()) {
                                        if (element.current() instanceof PlateElement plate && plate.matchSignTextures()) {
                                            context.roadSign().updateElement(element.id(),
                                                edited -> ((PlateElement) edited)
                                                    .withBackSource(newTexture.resize(plate.size())));
                                        }
                                    }
                                })
                        )
                ),
            // Add element controls
            smallHeader(t("clicksigns.editor.sign.elements.header")),

            button(t("+", "clicksigns.editor.sign.elements.addSymbol"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var center = context.roadSign().center();
                    var symbol = SignRegistries.SYMBOLS.get(RoadSign.DEFAULT_SYMBOL_TEXTURE);
                    var element = new SymbolElement(
                        center.x(), center.y(), Alignment.CENTER,
                        symbol
                    );
                    context.roadSign().addElement(element);
                }),
            button(t("+", "clicksigns.editor.sign.elements.addText"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var center = context.roadSign().center();
                    var element = new TextElement(
                        center.x(), center.y(), Alignment.CENTER,
                        "", 1.0f, TextStyle.DEFAULT
                    );
                    context.roadSign().addElement(element);
                }),
            button(t("+", "clicksigns.editor.sign.elements.addPlate"))
                .growWidth()
                .buttonColor(UiColor.LIME)
                .onClick(event -> {
                    var center = context.roadSign().center();
                    var element = new PlateElement(
                        center.x(), center.y(), Alignment.CENTER,
                        context.roadSign().frontSource().resize(8, 6),
                        context.roadSign().backSource().resize(8, 6),
                        true
                    );
                    context.roadSign().addElement(element);
                }),
            // Add tools
            smallHeader(t("clicksigns.editor.sign.tools.header")),
            button(t("⏪", "clicksigns.editor.sign.tools.resetTexts"))
                .growWidth()
                .buttonColor(UiColor.ORANGE)
                .onClick(event -> {
                    var elements = new ArrayList<>(context.roadSign().elements());
                    for (var element : elements) {
                        if (element.current() instanceof TextElement) {
                            context.roadSign().updateElement(element.id(),
                                edited -> ((TextElement) edited).withText(""));
                            // Regenerate id so that all caches are reset
                            context.roadSign().regenerateId(element.id());
                        }
                    }
                }),
            button(t("🗑", "clicksigns.editor.sign.tools.removeElements"))
                .growWidth()
                .buttonColor(UiColor.MAROON)
                .onClick(event -> {
                    var elements = new ArrayList<>(context.roadSign().elements());
                    for (var element : elements) {
                        context.roadSign().removeElement(element.id());
                    }
                }),
            smallHeader(t("clicksigns.editor.sign.export.header")),
            button(t("📤", "clicksigns.editor.sign.export.exportTemplate"))
                .growWidth()
                .buttonColor(UiColor.TEAL)
                .onClick(event -> {
                    new TemplateExportScreen(context.roadSign()).open();
                })
        );
    }
}
