package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a symbol that can be displayed on a road sign.
 *
 * @param identifier unique identifier for the symbol
 * @param texture    texture source for the symbol's texture
 */
public record Symbol(
        ResourceLocation identifier,
        TextureSource texture,
        @Nullable String category
) {
    /**
     * Resolves the category of this symbol, if it has one.
     *
     * @return the category of this symbol, or null if it has no category
     */
    public @Nullable SymbolCategory resolveCategory() {
        if (category == null) return null;
        return SymbolRegistry.getCategory(category);
    }
}
