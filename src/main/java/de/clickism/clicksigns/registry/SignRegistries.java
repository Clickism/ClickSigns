package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.TileSet;
import de.clickism.clicksigns.sign.template.Template;

public class SignRegistries {
    public static final CategorizedRegistry<TileSet> TILE_SETS = new CategorizedRegistry<>();
    public static final CategorizedRegistry<Symbol> SYMBOLS = new CategorizedRegistry<>(Symbol.ERROR_SYMBOL);
    public static final CategorizedRegistry<Template> TEMPLATES = new CategorizedRegistry<>();

    private SignRegistries() {
        // Singleton
    }
}
