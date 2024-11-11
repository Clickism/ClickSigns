package me.clickism.clicksigns.gui;

import me.clickism.clicksigns.Utils;
import me.clickism.clicksigns.gui.widget.RoadSignTemplateListWidget;
import me.clickism.clicksigns.gui.widget.TextureChangerWidget;
import me.clickism.clicksigns.sign.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.clickism.clicksigns.gui.GuiConstants.NOT_SELECTED_ALPHA;

@Environment(EnvType.CLIENT)
public class RoadSignTemplateSelectionScreen extends Screen {
    private static final int PADDING = 3;

    private final RoadSignEditScreen parent;

    private RoadSignTemplateListWidget templateList;
    private IconWidget iconWidget;
    private MultilineTextWidget infoWidget;
    private TextureChangerWidget textureChangerWidget;
    private RoadSignTemplate selectedTemplate = null;
    private final AtomicInteger textureIndex = new AtomicInteger(0);

    private static final SearchFilter<Integer> widthFilter = new SearchFilter<>();
    private static final SearchFilter<Integer> heightFilter = new SearchFilter<>();
    private static final SearchFilter<Set<Arrows>> arrowsFilter = new SearchFilter<>(new HashSet<>(3));

    protected RoadSignTemplateSelectionScreen(RoadSignEditScreen parent) {
        super(Text.translatable("clicksigns.text.road_sign_template_selection"));
        this.parent = parent;
    }

    public void setSelectedTemplate(@Nullable RoadSignTemplate selectedTemplate, int textureIndex) {
        this.selectedTemplate = selectedTemplate;
        this.textureIndex.set(textureIndex);
        remove(iconWidget);
        remove(infoWidget);
        remove(textureChangerWidget);
        if (selectedTemplate == null) return;
        iconWidget = addIconWidget(selectedTemplate);
        int textureCount = selectedTemplate.getTextures().size();
        textureChangerWidget = addTextureChangerWidget(textureCount);
        textureChangerWidget.setPosition(iconWidget.getX(), iconWidget.getY() + iconWidget.getHeight() + PADDING);
        textureChangerWidget.setTextureIndex(textureIndex + 1);
        infoWidget = addInfoWidget(selectedTemplate);
        infoWidget.setX(iconWidget.getX());
        infoWidget.setY(textureChangerWidget.getY() + textureChangerWidget.getHeight() + PADDING);
    }

    @Override
    protected void init() {
        templateList = new RoadSignTemplateListWidget(
                MinecraftClient.getInstance(),
                width / 2,
                height - 40,
                40,
                20,
                this
        );
        addDrawableChild(templateList);
        addFilters();
        addFilteredEntries();
        addConfirmButton();
    }

    private void addFilteredEntries() {
        templateList.children().clear();
        RoadSignTemplateRegistration.getTemplates(RoadSignCategory.PART).forEach(this::addFilteredTemplateEntry);
        RoadSignTemplateRegistration.getTemplates(RoadSignCategory.TEMPLATE).forEach(this::addFilteredTemplateEntry);
        RoadSignTemplateRegistration.getTemplates(RoadSignCategory.CUSTOM).forEach(this::addFilteredTemplateEntry);
    }

    private void addFilteredTemplateEntry(RoadSignTemplate template) {
        Integer width = widthFilter.getFilter();
        Integer height = heightFilter.getFilter();
        Set<Arrows> arrows = arrowsFilter.getFilter();
        if (width != null && template.getWidth() != width) return;
        if (height != null && template.getHeight() != height) return;
        if (arrows != null && !template.getArrows().containsAll(arrows)) return;
        RoadSignTemplateListWidget.Entry entry = new RoadSignTemplateListWidget.Entry(template);
        templateList.add(entry);
        if (parent.isSelectedTemplate(template)) {
            templateList.setSelected(entry);
        }
    }

    @Override
    public void close() {
        if (selectedTemplate != null) {
            parent.setTemplate(selectedTemplate, textureIndex.get());
        }
        GuiUtils.openScreen(parent);
    }

    private static final int PIXELS_PER_TEMPLATE_DIMENSIONS = 64;

    private IconWidget addIconWidget(RoadSignTemplate template) {
        int iconWidth = template.getWidth() * PIXELS_PER_TEMPLATE_DIMENSIONS;
        int iconHeight = template.getHeight() * PIXELS_PER_TEMPLATE_DIMENSIONS;

        float maxWidth = width / 2f - 20;
        float maxHeight = height / 2f - 20;
        float aspectRatio = (float) template.getWidth() / (float) template.getHeight();

        if (iconWidth > maxWidth) {
            iconWidth = (int) maxWidth;
            iconHeight = (int) (maxWidth / aspectRatio);
        }

        if (iconHeight > maxHeight) {
            iconHeight = (int) maxHeight;
            iconWidth = (int) (maxHeight * aspectRatio);
        }
        Identifier texture = template.getTextureOrError(textureIndex.get()).getFrontTexture();
        IconWidget widget = IconWidget.create(iconWidth, iconHeight, texture, iconWidth, iconHeight);
        widget.setX(width / 2 + 10);
        widget.setY(40);
        return addDrawableChild(widget);
    }

    private MultilineTextWidget addInfoWidget(RoadSignTemplate template) {
        return addDrawableChild(new MultilineTextWidget(getInfoText(template), textRenderer));
    }

    private TextureChangerWidget addTextureChangerWidget(int textureCount) {
        return addDrawableChild(new TextureChangerWidget(textRenderer, textureCount, (button, increment) -> {
            int index = Math.clamp(textureIndex.get() + increment, 0, textureCount - 1);
            textureIndex.set(index);
            textureChangerWidget.setTextureIndex(index + 1);
            setSelectedTemplate(selectedTemplate, index);
        }));
    }

    private Text getInfoText(RoadSignTemplate template) {
        return Text.translatable("clicksigns.text.width", template.getWidth())
                .append("\n").append(Text.translatable("clicksigns.text.height", template.getHeight()))
                .append("\n").append(Text.translatable("clicksigns.text.arrows", RoadSignTemplate.formatArrows(template.getArrows())))
                .append("\n").append(Text.translatable("clicksigns.text.variants",
                        template.getTextures().stream()
                                .map(RoadSignTexture::getName)
                                .collect(Collectors.joining(", "))))
                .append("\n").append(Text.translatable("clicksigns.text.category", Utils.titleCase(template.getCategory().name())))
                .append("\n").append(Text.translatable("clicksigns.text.pack", template.getPack().name()))
                .append("\n").append(Text.translatable("clicksigns.text.author", template.getAuthor()));
    }

    private void addFilters() {
        AxisOrganizer.horizontal(5, 20, true, PADDING)
                .organize(addFilterLabel(Text.translatable("clicksigns.text.filter_by").fillStyle(Style.EMPTY.withBold(true))))
                .organize(PADDING * 2, addFilterLabel(Text.translatable("clicksigns.text.width", "")))
                .organize(addTextFieldFilter(20, widthFilter, Utils::parseIntOrNull))
                .organize(PADDING * 2, addFilterLabel(Text.translatable("clicksigns.text.height", "")))
                .organize(addTextFieldFilter(20, heightFilter, Utils::parseIntOrNull))
                .organize(PADDING * 2, addFilterLabel(Text.translatable("clicksigns.text.arrows", "")))
                .organize(addArrowsFilterButton(Arrows.LEFT))
                .organize(addArrowsFilterButton(Arrows.RIGHT))
                .organize(addArrowsFilterButton(Arrows.FORWARD));
    }

    private TextWidget addFilterLabel(Text text) {
        TextWidget widget = addDrawableChild(new TextWidget(text, textRenderer));
        widget.setY(10 - widget.getHeight() / 2);
        return widget;
    }

    private <T> TextFieldWidget addTextFieldFilter(int width, SearchFilter<T> filter, Function<String, T> parser) {
        TextFieldWidget widget = new TextFieldWidget(textRenderer, 0, 0, width, 20, Text.literal(""));
        if (filter.getFilter() != null) {
            widget.setText(filter.getFilter().toString());
        }
        widget.setChangedListener((s) -> {
            filter.setFilter(parser.apply(s));
            addFilteredEntries();
        });
        return addDrawableChild(widget);
    }

    private ButtonWidget addArrowsFilterButton(Arrows arrows) {
        ButtonWidget widget = ButtonWidget.builder(
                        Text.literal(Utils.titleCase(arrows.name())),
                        button -> {
                            Set<Arrows> filter = arrowsFilter.getFilter();
                            if (!filter.contains(arrows)) {
                                filter.add(arrows);
                                button.setAlpha(1);
                            } else {
                                filter.remove(arrows);
                                button.setAlpha(NOT_SELECTED_ALPHA);
                            }
                            addFilteredEntries();
                        })
                .dimensions(0, 0, 50, 20)
                .build();
        if (arrowsFilter.getFilter() != null && arrowsFilter.getFilter().contains(arrows)) {
            widget.setAlpha(1);
        } else {
            widget.setAlpha(NOT_SELECTED_ALPHA);
        }
        return addDrawableChild(widget);
    }

    private ButtonWidget addConfirmButton() {
        return addDrawableChild(ButtonWidget.builder(
                                Text.translatable("clicksigns.text.confirm"),
                                button -> this.close()
                        ).dimensions(width - 100 - 10, height - 20 - 10, 100, 20)
                        .build()
        );
    }
}
