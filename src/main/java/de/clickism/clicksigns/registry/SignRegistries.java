package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.StaticTexture;
import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.TileSet;
import de.clickism.clicksigns.sign.color.ColorResolver;
import de.clickism.clicksigns.sign.template.Template;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry holder for sign-related registries.
 */
public class SignRegistries {
    /**
     * Registry for tile sets.
     */
    public static final CategorizedRegistry<TileSet> TILE_SETS = new CategorizedRegistry<>();
    /**
     * Registry for static textures.
     */
    public static final CategorizedRegistry<StaticTexture> STATIC_TEXTURES = new CategorizedRegistry<>();
    /**
     * Registry for symbols.
     */
    public static final CategorizedRegistry<Symbol> SYMBOLS = new CategorizedRegistry<>(Symbol.ERROR_SYMBOL);
    /**
     * Registry for templates.
     */
    public static final CategorizedRegistry<Template> RESOURCE_TEMPLATES = new CategorizedRegistry<>();
    /**
     * Registry for color resolvers.
     */
    public static final Map<ResourceLocation, ColorResolver> TILE_SET_COLOR_RESOLVERS = new HashMap<>();
    /**
     * Registry for static texture color resolvers.
     */
    public static final Map<ResourceLocation, ColorResolver> STATIC_TEXTURE_COLOR_RESOLVERS = new HashMap<>();
    /**
     * Registry for pack names. From namespace to pack name. Used for displaying the pack name in the UI.
     */
    public static final Map<String, String> PACK_NAMES = new HashMap<>();

    private SignRegistries() {
        // Singleton
    }
}
