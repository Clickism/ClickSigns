package de.clickism.clicksigns.ui.screen.texture;

import de.clickism.clicksigns.registry.Category;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.texture.TextureCategory;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.ui.UiConstants;
import de.clickism.clicksigns.ui.UiUtil;
import de.clickism.clicksigns.ui.components.CommonComponents;
import de.clickism.clickui.UiColor;
import de.clickism.clickui.UiScreen;
import de.clickism.clickui.layout.Align;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static de.clickism.clicksigns.ui.components.TextureButton.TEXTURE_SIZE;
import static de.clickism.clicksigns.util.ComponentUtil.t;

/**
 * A screen that allows the user to select a texture from a list of available textures.
 * The screen displays a title, a scrollable list of textures, and handles texture selection events
 */
public class TextureSelectScreen extends UiScreen<TextureSelectScreen> implements CommonComponents {
    private final Component title;
    private final Collection<TextureList.Entry> entries;
    private final UiColor backgroundColor;
    private Consumer<TextureList.Entry> onTextureSelected = entry -> {};
    private float textureScale = UiConstants.UI_SCALE;

    private final Map<Category<?>, Component> categoryDescriptions = new HashMap<>();

    public TextureSelectScreen(Component title, Collection<TextureList.Entry> entries, UiColor backgroundColor) {
        this.title = title;
        this.entries = entries;
        this.backgroundColor = backgroundColor;
    }

    public TextureSelectScreen onTextureSelected(Consumer<TextureList.Entry> onTextureSelected) {
        this.onTextureSelected = onTextureSelected;
        return this;
    }

    public TextureSelectScreen textureScale(float textureScale) {
        this.textureScale = textureScale;
        invalidateTree();
        return this;
    }

    public TextureSelectScreen categoryDescriptions(Map<Category<?>, Component> categoryDescriptions) {
        this.categoryDescriptions.clear();
        this.categoryDescriptions.putAll(categoryDescriptions);
        return this;
    }

    @Override
    public void build() {
        this.alignCenter()
            .grow()
            .padding(8)
            .childGap(8)
            .children(
                islandHeader(title),
                darkBoxOutlined()
                    .grow()
                    .scrollable(false)
                    .maxHeight(400)
                    .crossAlign(Align.CENTER)
                    .maxWidth(300)
                    .style(style()
                        .backgroundColor(backgroundColor))
                    .children(
                        new TextureList(entries, textureScale)
                            .onTextureSelected(texture -> {
                                onTextureSelected.accept(texture);
                                close();
                            })
                            .categoryDescriptions(categoryDescriptions)
                            .grow()
                    )
            );
    }

    /**
     * Creates a TextureSelectScreen that displays textures from the given categories.
     *
     * @param colorResolver     the color resolver to apply to the textures
     * @param categoriesToShow  the collection of texture categories to display in the screen
     * @param onTextureSelected a consumer that will be called when a texture is selected, receiving the selected TextureSource
     * @return a TextureSelectScreen instance configured to display the specified texture categories
     */
    public static TextureSelectScreen forTextureCategories(
        ColorResolver colorResolver,
        Collection<TextureCategory> categoriesToShow,
        Consumer<TextureSource> onTextureSelected
    ) {
        return forTextureCategories((TextureSource) null, colorResolver, categoriesToShow, onTextureSelected);
    }

    /**
     * Creates a TextureSelectScreen that displays textures from the given categories.
     *
     * @param background        the background texture source to determine the background color
     * @param colorResolver     the color resolver to apply to the textures
     * @param categoriesToShow  the collection of texture categories to display in the screen
     * @param onTextureSelected a consumer that will be called when a texture is selected, receiving the selected TextureSource
     * @return a TextureSelectScreen instance configured to display the specified texture categories
     */
    public static TextureSelectScreen forTextureCategories(
        @Nullable TextureSource background,
        ColorResolver colorResolver,
        Collection<TextureCategory> categoriesToShow,
        Consumer<TextureSource> onTextureSelected
    ) {
        var backgroundColor = background != null
            ? UiUtil.primaryColorOf(background.resolveImage(colorResolver))
            : UiColor.BLACK.alpha(0.5f);
        return forTextureCategories(backgroundColor, colorResolver, categoriesToShow, onTextureSelected);
    }

    /**
     * Creates a TextureSelectScreen that displays textures from the given categories.
     *
     * @param backgroundColor   the background color of the screen
     * @param colorResolver     the color resolver to apply to the textures
     * @param categoriesToShow  the collection of texture categories to display in the screen
     * @param onTextureSelected a consumer that will be called when a texture is selected, receiving the selected TextureSource
     * @return a TextureSelectScreen instance configured to display the specified texture categories
     */
    public static TextureSelectScreen forTextureCategories(
        UiColor backgroundColor,
        ColorResolver colorResolver,
        Collection<TextureCategory> categoriesToShow,
        Consumer<TextureSource> onTextureSelected
    ) {
        var categoryDescriptions = new HashMap<Category<?>, Component>();
        var entries = categoriesToShow.stream()
            .flatMap(category -> {
                var categoryEntries = entriesForCategory(category, colorResolver).toList();
                var description = categoryDescriptionFor(category);
                if (description != null) {
                    categoryEntries.forEach(entry -> {
                        categoryDescriptions.put(entry.category(), description);
                    });
                }
                return categoryEntries.stream();
            })
            .toList();
        return new TextureSelectScreen(t("clicksigns.ui.textureButton.textureMenu.header"), entries, backgroundColor)
            .textureScale(categoriesToShow.contains(TextureCategory.SYMBOL_TEXTURES)
                ? 3.0f
                : 2.0f)
            .categoryDescriptions(categoryDescriptions)
            .onTextureSelected(entry -> {
                if (entry == null) return;
                var identifier = entry.identifier();
                // Handle different categories
                if (SignRegistries.TILE_SETS.has(identifier)) {
                    var tileSet = SignRegistries.TILE_SETS.get(identifier);
                    onTextureSelected.accept(tileSet.textureSource(TEXTURE_SIZE, TEXTURE_SIZE));
                    return;
                }
                if (SignRegistries.STATIC_TEXTURES.has(identifier)) {
                    var staticTexture = SignRegistries.STATIC_TEXTURES.get(identifier);
                    onTextureSelected.accept(staticTexture.textureSource());
                    return;
                }
                if (SignRegistries.SYMBOLS.has(identifier)) {
                    var symbol = SignRegistries.SYMBOLS.get(identifier);
                    onTextureSelected.accept(symbol.textureSource());
                    return;
                }
            });
    }

    /**
     * Returns a stream of TextureList.Entry objects for the given texture category.
     *
     * @param category      the texture category to retrieve entries for
     * @param colorResolver the color resolver to apply to the textures
     * @return a stream of TextureList.Entry objects for the specified category
     */
    private static Stream<TextureList.Entry> entriesForCategory(TextureCategory category, ColorResolver colorResolver) {
        switch (category) {
            case TILE_SET_TEXTURES -> {
                return SignRegistries.TILE_SETS.all().stream()
                    .map(tileSet -> TextureList.entry(
                        tileSet.textureSource(TEXTURE_SIZE, TEXTURE_SIZE)
                            .resolve(colorResolver),
                        tileSet.identifier(),
                        tileSet.resolveCategory()
                    ));
            }
            case STATIC_TEXTURES -> {
                return SignRegistries.STATIC_TEXTURES.all().stream()
                    .map(staticTexture -> TextureList.entry(
                        staticTexture.textureSource().resolve(colorResolver),
                        staticTexture.identifier(),
                        staticTexture.resolveCategory()
                    ));
            }
            case SYMBOL_TEXTURES -> {
                return SignRegistries.SYMBOLS.all().stream()
                    .map(symbol -> TextureList.entry(
                        symbol.textureSource().resolve(colorResolver),
                        symbol.identifier(),
                        symbol.resolveCategory()
                    ));
            }
            default -> {
                return Stream.empty();
            }
        }
    }

    private static @Nullable Component categoryDescriptionFor(TextureCategory category) {
        return switch (category) {
            case TILE_SET_TEXTURES -> t("clicksigns.ui.textures.tileset").copy()
                .withStyle(ChatFormatting.DARK_GRAY);
            case STATIC_TEXTURES -> t("clicksigns.ui.textures.static").copy()
                .withStyle(ChatFormatting.DARK_GRAY);
            default -> null;
        };
    }
}
