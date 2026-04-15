package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.sign.template.theme.ColorResolver;
import de.clickism.clicksigns.sign.texture.Texture;
import de.clickism.clicksigns.sign.texture.TiledTexture;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

/**
 * Road sign class.
 *
 * @param texture     texture of the road sign
 * @param backTexture texture of the back of the road sign
 * @param elements    elements of the road sign
 */
public record RoadSign(
        Texture texture,
        Texture backTexture,
        List<RoadSignElement> elements
) {
    /**
     * Gets the color resolver for this road sign.
     *
     * @return the color resolver, or a default color resolver if the texture is not a tiled texture or the tileset could not be resolved
     */
    public ColorResolver colorResolver() {
        if (texture instanceof TiledTexture tiledTexture) {
            var tileSet = tiledTexture.resolveTileSet();
            if (tileSet != null) {
                return tileSet.theme().colorResolver();
            }
        }
        return ColorResolver.withDefault();
    }

    /**
     * Writer for packets
     */
    public static final FriendlyByteBuf.Writer<RoadSign> WRITER = (buf, element) -> {
        Texture.WRITER.accept(buf, element.texture());
        Texture.WRITER.accept(buf, element.backTexture());
        buf.writeCollection(element.elements(), RoadSignElement.WRITER);
    };

    /**
     * Reader for packets
     */
    public static final FriendlyByteBuf.Reader<RoadSign> READER = (buf) -> {
        Texture texture = Texture.READER.apply(buf);
        Texture backTexture = Texture.READER.apply(buf);
        var elements = buf.readList(RoadSignElement.READER);
        return new RoadSign(texture, backTexture, elements);
    };

    /**
     * Creates a new road sign with the given texture.
     * <p>
     * Keeps the existing back texture and elements.
     *
     * @param texture new texture for the road sign
     * @return a new road sign with the updated texture
     */
    public RoadSign withTexture(Texture texture) {
        return new RoadSign(texture, this.backTexture, this.elements);
    }

    /**
     * Creates a new road sign with the given back texture.
     * <p>
     * Keeps the existing front texture and elements.
     *
     * @param backTexture new back texture for the road sign
     * @return a new road sign with the updated back texture
     */
    public RoadSign withBackTexture(Texture backTexture) {
        return new RoadSign(this.texture, backTexture, this.elements);
    }

    /**
     * Creates a new road sign with the given elements.
     *
     * @param elements new elements for the road sign
     * @return a new road sign with the updated elements
     */
    public RoadSign withElements(List<RoadSignElement> elements) {
        return new RoadSign(this.texture, this.backTexture, elements);
    }
}
