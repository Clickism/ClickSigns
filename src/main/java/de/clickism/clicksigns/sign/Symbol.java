package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a symbol that can be displayed on a road sign.
 *
 * @param identifier    unique identifier for the symbol
 * @param textureSource texture source for the symbol's texture
 * @param defaultScale  default scale factor for the symbol's texture
 * @param categoryId    optional category identifier for the symbol
 */
public record Symbol(
    ResourceLocation identifier,
    TextureSource textureSource,
    float defaultScale,
    @Nullable ResourceLocation categoryId
) implements Categorized<Symbol> {
    /**
     * Error symbol to be used as fallback
     */
    public static final Symbol ERROR_SYMBOL = new Symbol(
        ClickSigns.identifier("error_symbol"),
        TextureSource.ofStatic(ClickSigns.identifier("error.png")),
        1f,
        null
    );

    @Override
    public CategorizedRegistry<Symbol> registry() {
        return SignRegistries.SYMBOLS;
    }
}
