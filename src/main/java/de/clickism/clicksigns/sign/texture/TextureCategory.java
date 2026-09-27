package de.clickism.clicksigns.sign.texture;

import java.util.Collection;
import java.util.List;

/**
 * Represents the category of a texture, which determines how it can be used, or where it's loaded from.
 */
public enum TextureCategory {
    /**
     * Tiled textures, that can be tiled to any size. Located in the tilesets directory.
     */
    TILE_SET_TEXTURES,
    /**
     * Static textures, that have a set size. Located in the static directory.
     */
    STATIC_TEXTURES,
    /**
     * Textures that are only allowed to be used as symbols on signs. Located in the symbols directory.
     */
    SYMBOL_TEXTURES;

    /**
     * Textures that are only allowed to be used on signs, as front and back textures.
     * Includes both {@link TILE_SET_TEXTURES} and {@link STATIC_TEXTURES}.
     */
    public static final Collection<TextureCategory> SIGN_TEXTURES = List.of(TILE_SET_TEXTURES, STATIC_TEXTURES);
}
