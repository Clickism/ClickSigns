package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.source.TextureSource;
import de.clickism.clicksigns.sign.texture.source.TiledTextureSource;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

/**
 * Road sign class.
 *
 * @param frontSource texture of the road sign
 * @param backSource  texture of the back of the road sign
 * @param elements    elements of the road sign
 */
public record RoadSign(
        TextureSource frontSource,
        TextureSource backSource,
        List<RoadSignElement> elements
) {
    /**
     * Gets the color resolver for this road sign.
     *
     * @return the color resolver, or a default color resolver if the texture is not a tiled texture or the tileset could not be resolved
     */
    public ColorResolver colorResolver() {
        // Use background's color resolver
        if (frontSource instanceof TiledTextureSource tiled) {
            var tileSet = tiled.resolveTileSet();
            if (tileSet != null) {
                return tileSet.colorResolver();
            }
        }
        return ColorResolver.withDefault();
    }

    /**
     * Resolves the front texture of the road sign.
     *
     * @return the resolved front texture
     */
    public Texture frontTexture() {
        return frontSource.resolve(colorResolver());
    }

    /**
     * Resolves the back texture of the road sign.
     *
     * @return the resolved back texture
     */
    public Texture backTexture() {
        return backSource.resolve(colorResolver());
    }

    /**
     * Writer for packets
     */
    public static final FriendlyByteBuf.Writer<RoadSign> WRITER = (buf, sign) -> {
        TextureSource.WRITER.accept(buf, sign.frontSource());
        TextureSource.WRITER.accept(buf, sign.backSource());
        buf.writeCollection(sign.elements(), RoadSignElement.WRITER);
    };

    /**
     * Reader for packets
     */
    public static final FriendlyByteBuf.Reader<RoadSign> READER = (buf) -> {
        var front = TextureSource.READER.apply(buf);
        var back = TextureSource.READER.apply(buf);
        var elements = buf.readList(RoadSignElement.READER);
        return new RoadSign(front, back, elements);
    };

    /**
     * Creates a new road sign with the given texture.
     * <p>
     * Keeps the existing back texture and elements.
     *
     * @param texture new texture for the road sign
     * @return a new road sign with the updated texture
     */
    public RoadSign withFront(TextureSource texture) {
        return new RoadSign(texture, this.backSource, this.elements);
    }

    /**
     * Creates a new road sign with the given back texture.
     * <p>
     * Keeps the existing front texture and elements.
     *
     * @param back new back texture for the road sign
     * @return a new road sign with the updated back texture
     */
    public RoadSign withBack(TextureSource back) {
        return new RoadSign(this.frontSource, back, this.elements);
    }

    /**
     * Creates a new road sign with the given elements.
     *
     * @param elements new elements for the road sign
     * @return a new road sign with the updated elements
     */
    public RoadSign withElements(List<RoadSignElement> elements) {
        return new RoadSign(this.frontSource, this.backSource, elements);
    }
}
