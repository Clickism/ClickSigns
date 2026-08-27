package de.clickism.clicksigns.ui;

import de.clickism.clicksigns.gui.GuiUtils;
import de.clickism.clicksigns.sign.Category;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clickui.Component;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.layout.Align;
import de.clickism.clickui.layout.Sizing;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TextureList extends Component<TextureList> {

    private final Map<Category<?>, List<Entry>> categoryToEntries;

    private Consumer<Entry> onTextureSelected = texture -> {};

    /**
     * Creates a new TextureList with the given entries.
     *
     * @param entries the collection of entries to display in the list
     */
    public TextureList(Collection<Entry> entries) {
        this.categoryToEntries = entries.stream()
            .collect(Collectors.groupingBy(Entry::category));
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
            .childGap(4)
            .crossAlign(Align.CENTER);
        add(box);

        categoryToEntries.forEach((category, entries) -> {
            box.add(text(category.name()));
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
                        GuiUtils.imageOf(texture)
                            .style(s -> s
                                .whenHovered(h -> h
                                    .border(UiColor.WHITE)
                                    .background(UiColor.WHITE_A20)))
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
    public record Entry(Texture texture, ResourceLocation identifier, Category<?> category) {
    }
}
