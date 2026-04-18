package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.registry.CategorizedRegistry;
import de.clickism.clicksigns.registry.SignRegistries;
import de.clickism.clicksigns.registry.SymbolRegistry;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.registry.Categorized;
import de.clickism.clicksigns.registry.Identifiable;
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
        @Nullable ResourceLocation categoryId
) implements Identifiable, Categorized<Symbol> {
    /**
     * Creates a new symbol identifier for a symbol included from another category, to avoid conflicts with the original symbol.
     *
     * @param location   original symbol identifier
     * @param categoryId id of the category to include the symbol in
     * @return a new resource location for the included symbol, based on the original location and the category name
     */
    public ResourceLocation identifierForCategory(ResourceLocation location, ResourceLocation categoryId) {
        var normalized = categoryId.getNamespace() + "__" + categoryId.getPath();
        return ResourceLocation.tryBuild(location.getNamespace(), location.getPath() + "__" + normalized);
    }

    /**
     * Checks if this symbol is the error symbol.
     *
     * @return true if this symbol is the error symbol, false otherwise
     */
    public boolean isError() {
        return this.identifier.equals(SymbolRegistry.ERROR_SYMBOL.identifier);
    }

    @Override
    public CategorizedRegistry<Symbol> registry() {
        return SignRegistries.SYMBOLS;
    }
}
