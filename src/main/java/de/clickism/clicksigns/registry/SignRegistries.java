package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.TileSet;
import de.clickism.clicksigns.sign.template.Template;

/**
 * Central registry holder for sign-related registries.
 */
public class SignRegistries {
    /**
     * Registry for tile sets.
     */
    public static final CategorizedRegistry<TileSet> TILE_SETS = new CategorizedRegistry<>();
    /**
     * Registry for symbols.
     */
    public static final CategorizedRegistry<Symbol> SYMBOLS = new CategorizedRegistry<>(Symbol.ERROR_SYMBOL);
    /**
     * Registry for templates.
     */
    public static final CategorizedRegistry<Template> RESOURCE_TEMPLATES = new CategorizedRegistry<>();

    private SignRegistries() {
        // Singleton
    }
}
