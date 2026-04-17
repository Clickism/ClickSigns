package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.texture.Texture;
import net.minecraft.network.FriendlyByteBuf;

public interface TextureSource {
    /**
     * The error texture to use when loading or generating a texture fails.
     */
    Texture ERROR_TEXTURE = new Texture(ClickSigns.identifier("error.png"), 32, 16);

    /**
     * Resolves the texture from this source, loading or generating it as necessary.
     *
     * @return the resolved texture
     */
    Texture resolve();

    // TODO: Refactor / make type safe by using a registry?
    /**
     * Writer for packets
     */
    FriendlyByteBuf.Writer<TextureSource> WRITER = (buf, texture) -> {
        int type = typeOf(texture);
        buf.writeInt(type);
        if (texture instanceof TiledTextureSource tiled) {
            // Tiled texture
            buf.writeResourceLocation(tiled.tileSetId());
            buf.writeInt(tiled.width());
            buf.writeInt(tiled.height());
        } else if (texture instanceof StaticTextureSource staticTextureSource) {
            // Static texture
            buf.writeResourceLocation(staticTextureSource.location());
        } else if (texture instanceof ColorizedTextureSource colorized) {
            // Colorized texture
            buf.writeResourceLocation(colorized.baseTexture());
            var fromColor = colorized.fromColor() != null ? colorized.fromColor().getRGB() : null;
            buf.writeNullable(fromColor, FriendlyByteBuf::writeInt);
            buf.writeInt(colorized.toColor().getRGB());
        } else {
            throw new IllegalArgumentException("Unknown texture source type: " + texture.getClass());
        }
    };

    /**
     * Reader for packets
     */
    FriendlyByteBuf.Reader<TextureSource> READER = (buf) -> {
        int type = buf.readInt();
        if (type == 1) {
            // Tiled texture
            var tileSetId = buf.readResourceLocation();
            var pixelWidth = buf.readInt();
            var pixelHeight = buf.readInt();
            return new TiledTextureSource(tileSetId, pixelWidth, pixelHeight);
        } else if (type == 2) {
            // Colorized texture
            var baseTexture = buf.readResourceLocation();
            var fromColor = buf.readNullable(FriendlyByteBuf::readInt);
            var toColor = buf.readInt();
            return new ColorizedTextureSource(baseTexture, fromColor, toColor);
        } else {
            // Static texture
            var location = buf.readResourceLocation();
            return new StaticTextureSource(location);
        }
    };

    private static int typeOf(TextureSource source) {
        if (source instanceof ColorizedTextureSource) return 2;
        if (source instanceof TiledTextureSource) return 1;
        if (source instanceof StaticTextureSource) return 0;
        throw new IllegalArgumentException("Unknown texture source type: " + source.getClass());
    }
}
