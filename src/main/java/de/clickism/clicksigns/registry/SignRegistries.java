package de.clickism.clicksigns.registry;

import de.clickism.clicksigns.sign.Symbol;
import de.clickism.clicksigns.sign.TileSet;

public class SignRegistries {
    public static final CategorizedRegistry<TileSet> TILE_SETS = new CategorizedRegistry<>();
    public static final CategorizedRegistry<Symbol> SYMBOLS = new CategorizedRegistry<>();

    private SignRegistries() {
        // Singleton
    }
}
