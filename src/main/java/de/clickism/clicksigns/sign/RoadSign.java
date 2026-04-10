package de.clickism.clicksigns.sign;

import de.clickism.clicksigns.sign.element.RoadSignElement;
import de.clickism.clicksigns.util.texture.Texture;
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
}
