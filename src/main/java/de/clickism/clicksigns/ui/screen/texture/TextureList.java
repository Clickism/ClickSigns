package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.registry.Category;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiComponent;
import de.clickism.clickui.layout.Align;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static de.clickism.clicksigns.util.ComponentUtil.t;

public class TextureList extends UiComponent<TextureList> implements CommonComponents {

    private final Map<Category<?>, List<Entry>> categoryToEntries;
    private final float textureScale;
    private Consumer<Entry> onTextureSelected = texture -> {};

    /**
     * Creates a new TextureList with the given entries.
     *
     * @param entries the collection of entries to display in the list
     */
    public TextureList(Collection<Entry> entries, float textureScale) {
        this.categoryToEntries = entries.stream()
            .collect(Collectors.groupingBy(Entry::category));
        this.textureScale = textureScale;
    }

    /**
     * Sets the callback to be invoked when a texture is selected.
     *
     * @param onTextureSelected the callback to be invoked when a texture is selected
     * @return this TextureList instance for method chaining
     */
    public TextureList onTextureSelected(Consumer<Entry> onTextureSelected) {
        this.onTextureSelected = onTextureSelected;
        return this;
    }

    @Override
    protected void build() {
        // Scrollable box
        var box = box()
            .grow()
            .scrollable(true)
            .padding(8)
            .childGap(8)
            .crossAlign(Align.CENTER);
        add(box);

        categoryToEntries.entrySet().stream()
            .sorted(Map.Entry.comparingByKey(Comparator.comparing(Category::name)))
            .forEach(mapEntry -> {
                var category = mapEntry.getKey();
                var entries = mapEntry.getValue();
                box.add(box()
                    .padding(4)
                    .growWidth()
                    .alignCenter()
                    .style(style()
                        .borderColor(UiColor.LIGHT_GRAY)
                        .backgroundColor(UiColor.BLACK_A80))
                    .children(
                        text(category.name())
                    ));
                // Add symbols
                var row = box()
                    .horizontal()
                    .wrapChildren(true)
                    .childGap(4)
                    .crossAlign(Align.START);

                box.add(row);
                entries.forEach(entry -> {
                    var texture = entry.texture();
                    for (int i = 0; i < 1; i++) {
                        row.add(
                            UiUtil.imageOf(texture, textureScale)
                                .tooltip(describeLeftClick(
                                    t("clicksigns.ui.textureList.tooltip.leftClick")
                                ))
                                .style(style()
                                    .whenHovered(style()
                                        .borderColor(UiColor.RED)
                                        .backgroundColor(UiColor.RED.alpha(0.1f))))
                                .onClick(event -> {
                                    onTextureSelected.accept(entry);
                                })
                        );
                    }
                });
            });
    }

    /**
     * Record representing an entry in the texture list, containing a texture, its identifier, and its category.
     *
     * @param texture    the texture to render
     * @param identifier the identifier of the texture, i.E. tile set name
     * @param category   the category of the texture
     */
    public record Entry(Texture texture, ResourceLocation identifier, @NotNull Category<?> category) {
    }
}
