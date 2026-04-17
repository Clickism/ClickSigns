package de.clickism.clicksigns.sign.texture.source;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.util.TypeKeyed;
import de.clickism.clicksigns.util.nbt.NbtReader;
import de.clickism.clicksigns.util.nbt.NbtWriter;
import net.minecraft.network.FriendlyByteBuf;

/**
 * Represents a source for a texture, which can be resolved to obtain the actual texture.
 * This allows for lazy loading and generation of textures as needed.
 */
public sealed interface TextureSource extends TypeKeyed permits StaticTextureSource, TiledTextureSource, ColorizedTextureSource {
    /**
     * The error texture to use when loading or generating a texture fails.
     */
    Texture ERROR_TEXTURE = new Texture(ClickSigns.identifier("error.png"), 32, 16);

    /**
     * Resolves the texture from this source, loading or generating it as necessary.
     *
     * @param colorResolver the color resolver to use for resolving colors in colorized textures, if needed
     * @return the resolved texture
     */
    Texture resolve(ColorResolver colorResolver);

    /**
     * Writer for packets
     */
    FriendlyByteBuf.Writer<TextureSource> PACKET_WRITER = (buf, texture) -> {
        var type = texture.typeKey();
        buf.writeUtf(type);
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
            buf.writeNullable(colorized.fromColor(), FriendlyByteBuf::writeUtf);
            buf.writeUtf(colorized.toColor());
        } else {
            throw new IllegalArgumentException("Unknown texture source type: " + texture.getClass());
        }
    };

    /**
     * Reader for packets
     */
    FriendlyByteBuf.Reader<TextureSource> PACKET_READER = (buf) -> {
        var type = buf.readUtf();
        return switch (type) {
            case TiledTextureSource.TYPE -> {
                var tileSetId = buf.readResourceLocation();
                var pixelWidth = buf.readInt();
                var pixelHeight = buf.readInt();
                yield new TiledTextureSource(tileSetId, pixelWidth, pixelHeight);
            }
            case ColorizedTextureSource.TYPE -> {
                var baseTexture = buf.readResourceLocation();
                var fromColor = buf.readNullable(FriendlyByteBuf::readUtf);
                var toColor = buf.readUtf();
                yield new ColorizedTextureSource(baseTexture, fromColor, toColor);
            }
            case StaticTextureSource.TYPE -> {
                var location = buf.readResourceLocation();
                yield new StaticTextureSource(location);
            }
            default -> throw new IllegalArgumentException("Unknown texture source type: " + type);
        };
    };

    /**
     * Writer for NBT
     */
    NbtWriter.Writer<TextureSource> NBT_WRITER = (tag, texture) -> {
        var typeKey = texture.typeKey();
        tag.putString("type", typeKey);
        if (texture instanceof TiledTextureSource tiled) {
            // Tiled texture
            tag.putResourceLocation("tileSet", tiled.tileSetId());
            tag.putInt("width", tiled.width());
            tag.putInt("height", tiled.height());
        } else if (texture instanceof StaticTextureSource staticTextureSource) {
            // Static texture
            tag.putResourceLocation("location", staticTextureSource.location());
        } else if (texture instanceof ColorizedTextureSource colorized) {
            // Colorized texture
            tag.putResourceLocation("baseTexture", colorized.baseTexture());
            if (colorized.fromColor() != null) {
                tag.putString("fromColor", colorized.fromColor());
            }
            tag.putString("toColor", colorized.toColor());
        } else {
            throw new IllegalArgumentException("Unknown texture source type: " + texture.getClass());
        }
    };

    /**
     * Reader for NBT
     */
    NbtReader.Reader<TextureSource> NBT_READER = (tag) -> {
        var type = tag.getString("type").orElseThrow();
        return switch (type) {
            case TiledTextureSource.TYPE -> {
                var tileSetId = tag.getResourceLocation("tileSet").orElseThrow();
                var pixelWidth = tag.getInt("width").orElseThrow();
                var pixelHeight = tag.getInt("height").orElseThrow();
                yield new TiledTextureSource(tileSetId, pixelWidth, pixelHeight);
            }
            case ColorizedTextureSource.TYPE -> {
                var baseTexture = tag.getResourceLocation("baseTexture").orElseThrow();
                var fromColor = tag.getString("fromColor").orElse(null);
                var toColor = tag.getString("toColor").orElseThrow();
                yield new ColorizedTextureSource(baseTexture, fromColor, toColor);
            }
            case StaticTextureSource.TYPE -> {
                var location = tag.getResourceLocation("location").orElseThrow();
                yield new StaticTextureSource(location);
            }
            default -> throw new IllegalArgumentException("Unknown texture source type: " + type);
        };
    };
}
