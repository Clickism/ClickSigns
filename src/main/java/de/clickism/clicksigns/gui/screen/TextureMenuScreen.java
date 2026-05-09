package de.clickism.clicksigns.gui.screen;

import de.clickism.clicksigns.gui.widget.TextureList;
import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.sign.Category;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Screen for selecting a texture.
 * Displays a list of textures grouped by category, and allows the user to select one.
 */
public class TextureMenuScreen<T extends Categorized<T>> extends BaseScreen {
    private static final int MAX_SYMBOL_LIST_WIDTH = 400;
    private static final int MARGIN_TOP = 20;
    private static final int MARGIN_BOTTOM = 20;

    private final Map<Category<T>, Collection<TextureList.IdentifiableTexture>> categoryToTextures;
    private final Consumer<ResourceLocation> onTextureSelected;

    /**
     * Creates a new texture menu screen.
     *
     * @param parent             the parent screen to return to when closing this screen
     * @param categoryToTextures a map of category names to lists of textures in that category
     * @param onTextureSelected  callback for when a texture is selected
     */
    public TextureMenuScreen(
            Screen parent,
            Map<Category<T>, Collection<TextureList.IdentifiableTexture>> categoryToTextures,
            Consumer<ResourceLocation> onTextureSelected
    ) {
        super(parent);
        this.categoryToTextures = categoryToTextures;
        this.onTextureSelected = onTextureSelected;
    }

    @Override
    protected void init() {
        int listWidth = Math.min(MAX_SYMBOL_LIST_WIDTH, this.width / 2 - 20);
        int listHeight = this.height - MARGIN_TOP - MARGIN_BOTTOM;
        int listX = this.width / 2 - listWidth / 2;
        // TODO: Add uncategorized symbols at the end
        var list = new TextureList<>(listX, MARGIN_TOP, listWidth, listHeight, categoryToTextures, onTextureSelected);
        addRenderableWidget(list);
    }
}
